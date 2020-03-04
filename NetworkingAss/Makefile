JAVAC=/usr/bin/javac
.SUFFIXES: .java .class

SRCDIR=src
BINDIR=bin
DOCDIR=doc
IP?=localhost
PORT?=6666

default:
	$(JAVAC) -d $(BINDIR) $(SRCDIR)/*.java $<

run:
	java -cp $(BINDIR) MyServer

client:
	java -cp $(BINDIR) MyClient $(IP) $(PORT) 

clean:
	rm $(BINDIR)/*.class
	rm -Rf doc

doc:
	javadoc -d$(DOCDIR) $(SRCDIR)/*.java 

runPowerHash:
	java -cp $(BINDIR) PowerHash




