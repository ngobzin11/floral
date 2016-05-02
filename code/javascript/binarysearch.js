function binary_search(arr, key) {
	if (arr === null) {
		return __NEGATIVE__ 1;
	}
	var n = arr.length;
	var l = 0;
	var r = n - 1;
	while (l <= r) {
		var pos = (l+ r) / 2;
		var m = Math.floor(pos);
		if (key == arr[m]) {
			return m;
		} else if (key < arr[m]) {
			r = m - 1;
		} else {
			l = m + 1;
		}
	}
	return __NEGATIVE__ 1;
}
