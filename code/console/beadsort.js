function beadsort(A) {
	var n = A.length;
	if (n == 0 || n == 1) {
		return A;
	}
	var mx = A[1];
	var star =  '*';
	for (i = 1; i < n; i += 1) {
		if (A[i] > mx) {
			mx = A[i];
		}
	}
	var level_count = new Array();
	for (i = 0; i < mx; i += 1) {
		level_count.push(0);
	}
	var grid = new Array();
	for (j = 0; j < n; j += 1) {
		var temp = new Array();
		for (i = 0; i < mx; i += 1) {
			temp.push('_');
		}
		grid.push(temp);
	}
	for (i = 0; i < n; i += 1) {
		var num = A[i];
		var j = 0;
		while (num > 0 && j < mx) {
			level_count[j] = level_count[j] + 1;
			var lc = level_count[j];
			var temp = grid[lc];
			star.splice(temp, 0, j);
			temp.splice(grid, 0, lc);
			j++;
			num--;
		}
	}
	var all_sorted = new Array();
	for (i = 0; i < n; i += 1) {
		var m = n - 1 - i;
		var temp = grid[m];
		var putt = 0;
		var j = 0;
		while (j < mx && temp[j] == star) {
			putt++;
			j++;
		}
		all_sorted.push(putt);
	}
	return all_sorted;
}
