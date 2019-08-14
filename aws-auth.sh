#!/usr/bin/env bash

myuser=`cat ~/.aws/identity|cut -d'=' -f2`
rolename="developer"
mfa_arn="arn:aws:iam::570551521311:mfa/$myuser"
# the developer role in the engineering dev account
arn="arn:aws:iam::895523100917:role/developer"
awsregion="eu-west-2"
sessionname=$myuser"AssumeRole"

if [[ ${BASH_SOURCE[0]} = "${0}" ]]; then
  echo "Assume AWS role"
  echo "Exiting as script ${BASH_SOURCE[0]} should be sourced to run within current environment : ( source ${BASH_SOURCE[0]} ) ..."
  exit 1
fi

unset AWS_ACCESS_KEY_ID
unset AWS_SECRET_ACCESS_KEY
unset AWS_SESSION_TOKEN
export AWS_DEFAULT_REGION=$awsregion

echo "Enter mfa code:"
read mfacode

creds=`aws sts get-session-token --serial-number $mfa_arn --token-code $mfacode | jq -r '.Credentials'`

export AWS_ACCESS_KEY_ID=`echo $creds | jq -r '.AccessKeyId'`
export AWS_SECRET_ACCESS_KEY=`echo $creds | jq -r '.SecretAccessKey'`
export AWS_SESSION_TOKEN=`echo $creds | jq -r '.SessionToken'`

# assume the developer role in the engineering dev account
mfacreds=`aws sts assume-role --role-arn $arn --role-session-name $sessionname --region $awsregion | jq -r '.Credentials'`
export AWS_ACCESS_KEY_ID=`echo $mfacreds | jq -r '.AccessKeyId'`
export AWS_SECRET_ACCESS_KEY=`echo $mfacreds | jq -r '.SecretAccessKey'`
export AWS_SESSION_TOKEN=`echo $mfacreds | jq -r '.SessionToken'`

echo "*** Assumed the developer role in the engineering dev account ***"
