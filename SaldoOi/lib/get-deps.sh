#!/bin/sh

verificaBaixa ()
{
  echo -n $1:' '
  if [ -f $1 ]; then
    echo -n 'encontrado, '
  else
    echo baixando.
    wget $2
    echo -n $1:' '
    if [ $? -ne 0 ]; then
      echo Falha ao baixar $1. Desistindo!
      rm -f $1
      ERROR=$((ERROR + 1))
      return
    else
      echo -n 'encontrado, '
    fi
  fi

  sha1sum --check $1.sha1 > /dev/null 2> /dev/null
  if [ $? -ne 0 ]; then
    echo CORRUPTO!
    rm -f $1
    ERROR=$((ERROR + 1))
  else
    echo ok.
  fi
}

ERROR=0

verificaBaixa slf4j-android-1.6.1-RC1.jar http://www.slf4j.org/android/slf4j-android-1.6.1-RC1.jar
verificaBaixa ormlite-core-4.40.jar http://ormlite.com/releases/4.40/ormlite-core-4.40.jar
verificaBaixa ormlite-android-4.40.jar http://ormlite.com/releases/4.40/ormlite-android-4.40.jar

if [ $ERROR -gt 0 ]; then
  echo '######'
  echo $ERROR 'erro(s) encontrado(s). Execute-me novamente.'
fi
