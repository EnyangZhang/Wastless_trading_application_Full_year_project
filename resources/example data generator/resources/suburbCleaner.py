file = open("suburbNames.txt", "r")
file1 = open("cleanSuburbs.txt", "a")

flag = False
stop = False

while not stop:
    try:
        item = file.readline()
        if len(item) == 0:
            if flag:
                stop = True
            flag = True
        else:
            flag = False
            item = item.strip()
            if len(item) != 1:
                better = item.split(",")[0]
                better = better.split("\n")[0]
                better += '\n'
                file1.write(better)
    except:
        continue