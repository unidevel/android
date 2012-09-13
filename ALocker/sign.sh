#!/bin/sh
if [ "x$1" == "x" ]; then
        echo "USAGE: sign APK"
        exit 1;
fi
FILE=$1
DIR=`dirname $FILE`
NAME=`basename $FILE`
rm -rf tmp
mkdir tmp
cd tmp
echo "$DIR/../$NAME"
unzip $DIR/../$NAME
rm -rf META-INF
cd ..
/jdk/bin/jar cvf $DIR/tmp.$NAME -C tmp .
/android/signapk platform.x509.pem platform.pk8 $DIR/tmp.$NAME $DIR/signed.$NAME