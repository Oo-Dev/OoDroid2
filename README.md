
Overview
---
Thanks for using new version of OoDroid.This program helps you stream your real time video with camera on you phone via wifi to your friends.

It is powered by [libstreaming](https://github.com/fyhertz/libstreaming).

Usage
---
Input the destination IP address(multicast supported) and press 'START'.Then,your friends can watch it with [Oodroid2-client](https://github.com/Oo-Dev/OoDroid-client)(just for demo).


Developers
---
Libstreaming lib is not available on 64-bit-core devices.If any ideas,welcome to contact us. 

Every time 'START' is pressed,rtsp is started and start to stream to the destination IP and a SDP distributor is started in the meanwhile, so as to distribute the session description files to clients in case that the destination IP address is a muticast address.