{
 split ($0, a, ";");
 if (length(trim(a[1]))>0) {
  print("\""trim(a[1])"\", "trim(a[2]));
  if (length(trim(a[3]))>0) {
    print("\""trim(a[1])"\", "trim(a[3]));
  }
 }
}

function trim(v) {
    ## Remove leading and trailing spaces (add tabs if you like)
    sub(/^ */,"",v)
    sub(/ *$/,"",v)
    return v
 } 
