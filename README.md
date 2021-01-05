# WSFTPPasswordRecovery
This is a simple script to recover forgotton ws_ftp passwords. It's very simple to use.

Find your .ini file with your saved sites: C:\Users\\[user_name]\Appdata\Roaming\Ipswitch\WS_FTP\Sites\ws_ftp.ini
Notice that if your site has multiple folders, then your ini file might reference other ini files. This script is only capable to decode one file at a time.

Then either compile the code or download the release and run the script with java -jar WSFTPPasswordRecovery.jar C:\\path\to\file.ini and it will hopefully output all the passwords in the file

It worked for my usecase, maybe it will work for you, maybe it need a little modification.
