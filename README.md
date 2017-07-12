# NFCTag
The TAG emulation application

This application displays a checkbox that toggles every 3 seconds.
Depending on the state of the checkbox the application will send an NFC payload information with the following message:
- nothing - if the checkbox is selected
- unlock  - if the checkbox is not selected
The payload that will be sent can be modified by manualy changing the toggle, but the toggle state will be reset when the 3 seconds will pass.

The appliction is set to open the application with the following package "com.kisi.acai.nfcreader"
