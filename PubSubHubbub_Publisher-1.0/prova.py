#!/usr/bin/env python
#
# Copyright 2009 Google Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

"""Simple Publisher client for PubSubHubbub.

Example usage:

  from pubsubhubbub_publish import *
  try:
    publish('http://pubsubhubbub.appspot.com',
            'http://example.com/feed1/atom.xml',
            'http://example.com/feed2/atom.xml',
            'http://example.com/feed3/atom.xml')
  except PublishError, e:
    # handle exception...

Set the 'http_proxy' environment variable on *nix or Windows to use an
HTTP proxy.
"""

__author__ = 'bslatkin@gmail.com (Brett Slatkin)'

import urllib
import urllib2

from pubsubhubbub_publish import *
try:
  publish('http://pubsubhubbub.appspot.com',
          'http://example.com/feed1/atom.xml',
          'http://example.com/feed2/atom.xml',
          'http://example.com/feed3/atom.xml')
except PublishError, e:
    # handle exception...
   print 'errore'
