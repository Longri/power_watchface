Power WatchFace 
===================================

A Watch Face, which focusses on the battery life.

I started the project to create for my LG-Watch R  a Watch Face, which indicates the important information for me and still protects the battery life.


---
Develop
----------

In Application Project a file with personal API key is missing!
To start the project, you must create the file "secure.xml".

/Application/src/main/res/values/ secure.xml

This contains two String key for the Open Weather API Key and Donate URL!

    <?xml version="1.0" encoding="utf-8"?>
    <!--
      Copyright (C) 2015-2016 longri.de
    
      Licensed under the : GNU General Public License (GPL);
      you may not use this file except in compliance with the License.
      You may obtain a copy of the License at
    
           http://www.gnu.org/licenses/gpl.html
    
      Unless required by applicable law or agreed to in writing, software
      distributed under the License is distributed on an "AS IS" BASIS,
      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
      See the License for the specific language governing permissions and
      limitations under the License.
     -->
    <resources>
        <string name="openweatherApiKey">????????</string>
        <string name="donateUrl">https://www.paypal.com/cgi-bin/...</string>
    </resources>

License
-------

Copyright (C) 2015-2016 Longri.de
 
  Licensed under the : GNU General Public License (GPL);
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

[http://www.gnu.org/licenses/gpl.html](http://www.gnu.org/licenses/gpl.html)

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
