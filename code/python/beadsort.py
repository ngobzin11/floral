def beadsort(A):
	n = len(A)
	if n == 0 or n == 1:
		return A
	
	mx = A[1]
	star =  '*'
	for i in range(1, n, 1):
		if A[i] > mx:
			mx = A[i]
		
	
	level_count = []
	for i in range(0, mx, 1):
		level_count.append(0)
	
	grid = []
	for j in range(0, n, 1):
		temp = []
		for i in range(0, mx, 1):
			temp.append('_')
		
		grid.append(temp)
	
	for i in range(0, n, 1):
		num = A[i]
		j = 0
		while num > 0 and j < mx:
			level_count[j] = level_count[j] + 1
			lc = level_count[j]
			temp = grid[lc]
			temp.insert(j, star)
			grid.insert(lc, temp)
			j = j + 1
			num = num - 1
		
	
	all_sorted = []
	for i in range(0, n, 1):
		m = n - 1 - i
		temp = grid[m]
		putt = 0
		j = 0
		while j < mx and temp[j] == star:
			putt = putt + 1
			j = j + 1
		
		all_sorted.append(putt)
	
	return all_sorted

