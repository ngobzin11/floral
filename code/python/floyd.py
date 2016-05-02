def floyd(W):
	n = len(W)
	D = arrays_copy(W)
	for k in range(0, n, 1):
		D_k = D[k]
		for i in range(0, n, 1):
			D_i = D[i]
			D_ik = D_i[k]
			for j in range(0, n, 1):
				sm = D_ik + D_k[j]
				if sm < D_i[j]:
					D_i[j] = sm
				
			
			D[i] = D_i
		
	
	return D

def arrays_copy(A):
	res = []
	for a_i in A:
		res.append(a_i)
	
	return res

