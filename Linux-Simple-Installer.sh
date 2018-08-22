#!/bin/bash

sudo add-apt-repository ppa:webupd8team/java -y
sudo LC_ALL=C.UTF-8 add-apt-repository ppa:ondrej/php -y
sudo apt-get update
sudo apt remove mysql-server mysql-server-5.7 mysql-client -y
sudo apt-get install nano ant git oracle-java8-installer mariadb-server mariadb-client apache2 -y
sudo apt-get autoremove -y

sudo mysql_secure_installation
clear
echo "Please enter your MySQL password: "
read -s pass
#sudo mysql -uroot -Bse "use mysql;update user set plugin='mysql_native_password' where user='root';flush privileges;"

sudo apt-get install phpmyadmin php-fpm php-mysql php-mbstring php-gettext -y
sudo phpenmod mbstring
sudo systemctl restart apache2

git clone git://github.com/Open-RSC/Game.git
sudo chmod -R 777 .

sudo ant -f server/build.xml compile
sudo ant -f client/build.xml compile

mysql -u"root" -p"$pass" < "Databases/openrsc.sql" &>/dev/null
mysql -u"root" -p"$pass" < "Databases/openrsc_config.sql" &>/dev/null
mysql -u"root" -p"$pass" < "Databases/openrsc_tools.sql" &>/dev/null
mysql -u"root" -p"$pass" < "Databases/openrsc_logs.sql" &>/dev/null
sudo cp ./client/Open_RSC_Client.jar /var/www/html

sudo nano server/config/config.xml

myip="$(dig +short myip.opendns.com @resolver1.opendns.com)"
echo "The installation script has completed."
echo "You should be able to download the client at: http://${myip}/Open_RSC_Client.jar in your web browser."
