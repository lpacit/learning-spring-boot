from email.mime.text import MIMEText
                from email.mime.image import MIMEImage
                from email import encoders
                import botocore
                import mimetypes
                import boto3
                import email
                import os
                import re
                from botocore.exceptions import ClientError
                from email import policy

                  
                def lambda_handler(event, context):
                    
                    s3 = boto3.client('s3')
                    source_bucket = event['Records'][0]['s3']['bucket']['name']
                    key = event['Records'][0]['s3']['object']['key']

                    try:
                        data = s3.get_object(Bucket=source_bucket, Key=key)
                        content = data['Body'].read()
                        print('Retrieved S3 Object: {}'.format(key))


                    except botocore.exceptions.ClientError as error:
                        if error.response['Error']['Code'] == 'NoSuchBucket':
                            print('Bucket {} does not exist.'.format(source_bucket))
                        else:
                            raise error
                    
                    except botocore.exceptions.ClientError as error:
                        if error.response['Error']['Code'] == 'NoSuchKey':
                            print('Error retrieving object {} from source bucket {}. Verify existence of the object.'.format(key, source_bucket))
                        else:
                            raise error
                    
                    original_message = email.message_from_bytes(content, policy=policy.default)
                    create_message(original_message)
                    return "Execution completed: email parsed correctly, attachment saved."


                def create_message(original_message):
                    
                    message = email.message.EmailMessage()

                    for key, value in original_message.items():
                        head = key.replace('\n', '')
                        val = value.replace('\n', ' ')
                        message.add_header(head.strip(), val)
                        
                    messageId = re.findall("\\<(.*?)\\>", message['message-id'])[0]

                    body, html = get_body(original_message)
                    plain_text_body = MIMEText(body, _subtype='plain', _charset='iso-8859-1')
                    html_part = MIMEText(html, _subtype='html', _charset='iso-8859-1')
                    message.attach(plain_text_body)
                    message.attach(html_part)


                    att_list = get_attachment_list(original_message)
                    inline_dict = get_inline_dict(original_message)

                    for i in att_list:
                        add_empty_attachment(message, i)

                    for k,v in inline_dict.items():
                        add_empty_inline(message,k,v)

                    save_file(message, 'message.eml', message.as_bytes(), 'text')


                def get_body(message):
                    if message.is_multipart():
                        for part in message.walk():
                            ctype = part.get_content_type()
                            cdispo = str(part.get('Content-Disposition'))

                            if ctype == 'text/plain' and 'attachment' not in cdispo:
                                # body = part.get_payload(decode=True).decode('latin1')
                                bytes = part.get_payload(decode=True)
                                charset = part.get_content_charset('iso-8859-1')
                                body = bytes.decode(charset, 'replace')
                                
                            if ctype == 'text/html' and 'attachment' not in cdispo:
                                b = part.get_payload(decode=True)
                                charset = part.get_content_charset('us-ascii')
                                html = b.decode(charset, 'replace')
                                # message.attach(MIMEText(html, "html"))

                    else:
                        bytes = message.get_payload(decode=True)
                        charset = part.get_content_charset('iso-8859-1')
                        body = bytes.decode(charset, 'replace')
                        html = ""
                    
                    return body, html
                    

                def get_attachment_list(message):
                    att_list = []
                    if message.is_multipart():
                        for part in message.walk():
                            cdispo = str(part.get('Content-Disposition'))
                            filename = str(part.get('filename'))

                            if 'attachment' in cdispo:
                                filename = part.get_filename()
                                attachment = part.get_payload(decode=True)

                                save_file(message, filename, attachment, 'attachment')
                                
                                att_list.append(filename)
                    else:
                        attachment = None
                    
                    return att_list

                def get_inline_dict(message):
                    inline_dict = {}
                    if message.is_multipart():
                        for part in message.walk():
                            cdispo = str(part.get('Content-Disposition'))
                            cid = str(part.get('Content-ID'))

                            if ('inline' in cdispo or '.png' in cid):
                                attachment = part.get_payload(decode=True)
                                filename = part.get_filename()
                                save_file(message, filename, attachment, 'inline')

                                inline_dict[filename] = part.get('Content-ID')
                            
                    else:
                        attachment = None
                    
                    return inline_dict

                def add_empty_attachment(message, filename):
                    mime_type, encoding = mimetypes.guess_type(filename)
                    message.add_attachment(b'', maintype=mime_type.split("/")[0],
                                                        subtype=mime_type.split("/")[1],
                                                        filename=filename)

                def add_empty_inline(message, filename, contentid):
                    mime_type, encoding = mimetypes.guess_type(filename)

                    img = MIMEImage(b'', mime_type.split("/")[0])
                    img.add_header('Content-Id', '%s' % contentid )
                    img.add_header("Content-Disposition", "inline", filename=filename) 
                    message.attach(img)

                def save_file(message, filename, payload, type):

                    messageId = re.findall("\\<(.*?)\\>", message['message-id'])[0]

                    try:
                        fromAddress = re.findall("\\<(.*?)\\>", message['from'])[0]
                    except IndexError:
                        fromAddress = message['from']

                    source_path = '/tmp/{}'.format(filename)
                    destination_path = '{}/{}/{}/{}'.format(fromAddress, messageId, type, filename)

                    with open(source_path, 'wb') as f:
                        f.write(payload)
                    
                    if type.__eq__('attachment') or type.__eq__('inline'):
                        bucket = os.environ['S3_Attachment_Bucket']
                    else:
                        bucket = os.environ['S3_Message_Bucket']
                    
                    upload_to_S3(source_path, destination_path, bucket, filename, type)
                    
                def upload_to_S3(source_path, destination_path, destination_bucket, filename, type):
                    s3r = boto3.resource('s3')

                    try:
                        s3r.meta.client.upload_file(source_path, destination_bucket, destination_path)
                        print('Uploaded {} file \'{}\' into bucket {}'.format(type, filename, destination_bucket))
                    except ClientError as e:
                        print(e)
                    except FileNotFoundError:
                        print("The file was not found")