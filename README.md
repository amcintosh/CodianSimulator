# Codian Simulator

This is a small project to simulate the API of a Cisco TelePresence MCU (née Tandberg Codian MCU). 
This is **not** a video bridge. 

Obtaining multiple MCUs for development and lab testing can be prohibitively expensive, 
so the purpose of this is to aid in development and testing of code that requires multiple bridges 
(load balancing, monitoring, etc.). This is meant to supplement an existing bridge for development 
and it is not recommended to use this simulator without an actual bridge also available.


### Usage

The application is meant to be a drop-in for an Cisco TelePresence MCU and as such should match 
Cisco's [Programming Guides](http://www.cisco.com/en/US/products/ps11341/products_programming_reference_guides_list.html)
(Currently coded for API version 2.9). 

I have thus far only coded the endpoints that I have actually needed myself, which include:

- device.query
- conference.create
- conference.destroy
- conference.enumerate
- ~~conference.paneplacement.modify~~
- participant.add
- participant.enumerate
- participant.modify

Some of the return data is hard-coded if the endpoint that configure it aren't yet implemented. 
You may need some customization if your uses are particularly different than mine.


### Build

There is a very straight forward ant build file which will produce: /dist/CodianSimulator.war
I have only tested with tomcat6/7.


### Design Notes

As the primary purpose of this is to replicate the API endpoints of an actual Cisco TelePresence MCU, 
pretty much everything beyond the front facing services was secondary. I have opted for a sqlite
database for persistence that is destroyed and recreated on undeploy/deploy (you could of course 
change the base directory so that it sticks around). The design is very light, basically throwing the
data into the database or returning it with some light massaging. Features that I haven't needed 
that aren't particularly straight forward, or are not really required due to the nature of the application 
(not really a bridge) may be hardcoded.


### Todo

- Finish things (see endpoints above)
- Simple UI, for automated tests to check conferences against
- Probably convert build to Gradle


### License

Source code is licensed under the
[GNU General Public License](http://www.gnu.org/licenses/gpl.html).
