incredimail-converter-cmdline
=============================
A small tool to extract emails from the Incredimail2 format to eml files.
Deleted mails are not supported.
Only the Incredimail2 format is supported (upgrade to Incredimail2 first, this should upgrade the data before converting.)


RUN
---

  build/install/incredimail-converter-cmdline/bin/incredimail-converter-cmdline <incredimail dir> <empty output dir>
	
where incredimail dir is the Message Store directory under your Incredimail identity
and <empty output dir> is an empty or not existing directory, where the eml files will be placed.	
	

BUILD
-----

  gradle clean installApp
  
  