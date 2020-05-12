#!/bin/bash

# Copyright (c) 2019 Abex
# All rights reserved.
#
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions are met:
#
# 1. Redistributions of source code must retain the above copyright notice, this
#    list of conditions and the following disclaimer.
# 2. Redistributions in binary form must reproduce the above copyright notice,
#    this list of conditions and the following disclaimer in the documentation
#    and/or other materials provided with the distribution.
#
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
# ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
# WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
# DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
# ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
# (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
# LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
# ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
# (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
# SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

set -e -x

: '
env:
  -FORCE_BUILD: example-external-plugin
'

if [[ "$FORCE_BUILD" == "ALL" ]]; then
	./rebuild_all.sh
	exit
elif [[ -n "${FORCE_BUILD+x}" ]]; then
	for FI in $(echo "$FORCE_BUILD" | tr ',' '\n'); do
		./build_plugin.sh "plugins/$FI"
	done
	./build_manifest.sh
	exit
fi

PLUGIN_CHANGE=
while read -r FI ; do
	if [[ $FI =~ ^plugins/.*$ ]]; then
		[ -e "$FI" ] && ./build_plugin.sh "$FI" < /dev/null
		PLUGIN_CHANGE=true
	elif [[ "$FI" == "runelite.version" ]]; then
		./rebuild_all.sh < /dev/null
	fi
done < <(git diff --name-only "$TRAVIS_COMMIT_RANGE")

if [[ "$PLUGIN_CHANGE" == true ]]; then
	./build_manifest.sh
fi