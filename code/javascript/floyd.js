function floyd(W) {
	var n = W.length;
	var D = arrays_copy(W);
	for (var k = 0; k < n; k += 1) {
		var D_k = D[k];
		for (var i = 0; i < n; i += 1) {
			var D_i = D[i];
			var D_ik = D_i[k];
			for (var j = 0; j < n; j += 1) {
				var sm = D_ik + D_k[j];
				if (sm < D_i[j]) {
					D_i[j] = sm;
				}
			}
			D[i] = D_i;
		}
	}
	return D;
}
function arrays_copy(A) {
	var res = new Array();
	for (a_i in A) {
		res.push(a_i);
	}
	return res;
}
