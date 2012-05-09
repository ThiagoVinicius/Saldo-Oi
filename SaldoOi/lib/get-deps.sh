#!/bin/sh

verificaBaixa () 
{
  echo -n Verificando $1...
  sha1sum --check $1.sha1 > /dev/null 2> /dev/null
  if [ $? -ne 0 ]; then
    echo Falha
    if [ -f $1 ]; then
      rm $1
    fi
    wget $2
  else
    echo OK
  fi 
}

verificaBaixa slf4j-android-1.6.1-RC1.jar http://www.slf4j.org/android/slf4j-android-1.6.1-RC1.jar
verificaBaixa ormlite-core-4.30.jar http://ormlite.com/releases/4.30/ormlite-core-4.30.jar
verificaBaixa ormlite-android-4.30.jar http://ormlite.com/releases/4.30/ormlite-android-4.30.jar
