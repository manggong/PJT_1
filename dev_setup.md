### dev set-up

jdk 설치 - sftp

jar 이동 - sftp

http / https 오픈

sudo firewall-cmd --permanent --add-service=http
sudo firewall-cmd --permanent --add-service=https
sudo firewall-cmd --reload

프로젝트 빌드
./gradlew clean build

ssh 설정
sudo dnf install openssh-server -y
sudo systemctl enable sshd
sudo systemctl start sshd

포트 오픈
sudo firewall-cmd --permanent --add-port=8080/tcp
sudo firewall-cmd --reload

기동
nohup java -jar swagger_test-0.0.1-SNAPSHOT.jar > app.log 2>&1 &
