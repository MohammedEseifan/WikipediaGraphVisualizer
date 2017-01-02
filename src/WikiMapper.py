
import urllib2,re,sys
from bs4 import BeautifulSoup
from HTMLParser import HTMLParser


def isValidURL(u):
	invalidCharacters = ["#","File",".org","http","/w/"]
	if u==None: return False 
	for c in invalidCharacters:
		if c in u:
			return False
	return True


startUrl = "http://en.wikipedia.org/wiki/Shortest_path_problem"
maxNumHops = int(sys.argv[1])
print "Mapping up to %i hops."%(maxNumHops)
adjDic = {}
linkNames = {}
frontier = [startUrl.replace("http://en.wikipedia.org","")]
counter = 0
pagesVisited = 0
alreadyVisited = []
while counter<maxNumHops:
	temp = []
	for pageToVisit in frontier:
		try:
			soup = BeautifulSoup(urllib2.urlopen("http://en.wikipedia.org"+pageToVisit).read()) 
			soup = BeautifulSoup(str(soup.find("div", {"class": "mw-body-content"}))) #creating Beautiful soup object for the main article
			pagesVisited+=1
			alreadyVisited.append(pageToVisit)
			print pagesVisited
			if pageToVisit not in adjDic: adjDic[pageToVisit]=[] #adding it to the graph if its not there
			for link in soup.find_all('a'): #looping through all of the links
				l = link.get('href')
				if l in alreadyVisited: continue
				if isValidURL(l):
					temp.append(str(l))
					adjDic[pageToVisit].append(str(l))
					
					#link = str(link)
					#name = link[link.find('>')+1:link.rfind('<')]
					#print l,name
		except Exception, e:
			print pageToVisit
			print str(e)
		
	counter +=1
	frontier=temp[:]
f = open('wikiMap '+str(maxNumHops)+" hops.txt","w")
f.write(str(adjDic))
f.close()
print "Done"