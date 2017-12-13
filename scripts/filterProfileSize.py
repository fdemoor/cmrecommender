import os

dataset = "ml-100k"
filename = "ratings.csv"
path = "../datasets/" + dataset + "/"
dest = "../datasets/" + dataset + "_filtered/"

mid = 75 # Middle value
d = 35 # Accepted values will be in the range [mid - d, mid + d]

os.system('mkdir -p ' + dest)

DATA = {}
with open(path + filename, 'r') as f:
	for line in f:
		data = line.rstrip("\n").split(",")
		user = data[0]
		item = data[1]
		rating = data[2]
		if user not in DATA:
			DATA[user] = []
		DATA[user].append((item, rating))
		
with open(dest + filename, 'w') as f:
	for user, pairs in DATA.items():
		if abs(mid - len(pairs)) <= d:
			for pair in pairs:
				f.write(user + "," + pair[0] + "," + pair[1] + "\n")

