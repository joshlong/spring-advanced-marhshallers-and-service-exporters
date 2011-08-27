Notes on other things to potentially support:
 -- google snappy compression support
 -- jboss remoting endpoint
 -- edmunds' particular version of thrift?

If you're new to Thrift, then you might check the apache website to see if they've yet finished that tutorial (doubt it.)

Here's one that's quite good: http://www.thrift.pl/Thrift-tutorial-installation.html
Also, see http://blog.newitfarmer.com/software-develop/open-source/869/install-thrift-on-linux/
Also, http://diwakergupta.github.com/thrift-missing-guide/
Also, http://unknownerror.net/2011-08/thrift-java-server-and-client-examples-50052

# naturally, this all presumes you've got a JDK >= 5 installed.
# I did the following on my box to get it the point where it looked like it worked)
sudo apt-get install ant1.7
sudo apt-get install python-dev
sudo apt-get install byacc
sudo apt-get install libboost-filesystem1.42.0 libboost-thread1.42.0
sudo apt-get install libboost-filesystem1.42.0 libboost-thread1.42.0 libboost-system1.42.0
sudo apt-get install flex
sudo apt-get install libboost-dev libboost-test1.42-dev libevent-dev automake libtool flex bison pkg-config g++
sudo apt-get install automake
sudo apt-get install python-dev python-twisted ruby-dev librspec-ruby rake rubygems php5-dev php5-cli libbit-vector-perl libglib2.0-dev
sudo apt-get install mono-gmcs libmono-dev
sudo apt-get install ghc6 cabal-install libghc6-binary-dev libghc6-network-dev libghc6-http-dev
sudo apt-get install libboost-dev libboost-test1.40-dev libevent-dev automake libtool flex bison pkg-config g++


 wget http://www.motorlogy.com/apache//thrift/0.7.0/thrift-0.7.0.tar.gz #download it from Apache
 tar zxpf thrift-0.7.0.tar.gz
 cd thrift-0.7.0
 chmod a+x configure
 ./configure
 make # assuming that goes with no problems
 sudo make install




