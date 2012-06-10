#!/bin/sh

git --version > /dev/null
if [ $? -ne 0 ]; then
  echo "Git não encontrado!"
  exit 1
fi

hash=`git rev-parse --short HEAD`
if [ $? -ne 0 ]; then
  echo "Não estamos em um repositório git!"
  hash=""
fi

estado=`git status --porcelain`
if [ "${estado}" != "" ] && [ "$hash" != "" ]; then
  hash=${hash}"*"
fi

if [ "$hash" != "" ]; then
  texto="git ${hash}"
else
  texto=""
fi

sed -i s/"\(.*\"info_scm\">\).*\(<.*\)"/"\\1${texto}\\2"/g "${@}"
