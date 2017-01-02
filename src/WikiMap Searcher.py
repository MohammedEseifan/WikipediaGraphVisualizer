f = open("wikiMap 2 hops.txt",'r')
dict = eval(f.read().strip())
start = raw_input("Enter starting webpage.").replace("http://en.wikipedia.org","")
end = raw_input("Enter ending webpage.").replace("http://en.wikipedia.org","")
visited = []
parent = {start:None}
if start in dict:
	frontier = [start]
	while len(frontier)>0:
		temp =[]
		for f in frontier:
			if not f in visited:
				if f not in dict:
					print "Search is incomplete because certain webpages were not mapped"
				for u in dict[f]:
					temp.append(u)
					if u in parent:
						parent[u].append(f)
					else:
						parent[u]=[f]
					if u == end:
						print "found"
						print parent
						temp=[]
						break
		frontier=temp[:]

	if not end in parent:
		pass

else:
	print "This webpage hasnt been mapped"