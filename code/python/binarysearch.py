# Binary Search:
#	Implements non-recursive binary search
#	Input: An array A, sorted in ascending order 
#		and a search key K
#	Output: An index of the array's element that 
# 		is equal to K or -1 if there is no 
#		such element
def binary_search(arr, key):
	if arr == None:
		return __NEGATIVE__ 1
	
	n = len(arr)
	l = 0
	r = n - 1
	while l <= r:
		pos = (l+ r) / 2
		m = math.floor(pos)
		if key == arr[m]:
			return m
		elif key < arr[m]:
			r = m - 1
		else:
			l = m + 1
		
	
	return __NEGATIVE__ 1

