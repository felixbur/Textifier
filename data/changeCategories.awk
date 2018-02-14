{
split ($0, a, ",");
s=a[1];
split(a[1],b,"\"");
s=trim(b[2]);
if (length(s)>0) {
c=a[2];
  if (c~/0/) {
   c="noap";
 } else if (c~/1/) {
   c="kdsv";
 } else if  (c~/2/) {
   c="kdsv"; 
 } else if  (c~/3/) {
   c="kdsv"; 
 } else if  (c~/4/) {
    c="kdsv";
 } else if  (c~/5/) {
    c="undefined 5";
 } else if  (c~/6/) {
    c="umzg";
 } else if  (c~/7/) {
    c="strg";
 } else if  (c~/8/) {
    c="rchn";
 } else if  (c~/a/) {
    c="knbr";
 } else if  (c~/b/) {
    c="untr";
 } else if  (c~/c/) {
    c="undefined c";
 } else if  (c~/d/) {
    c="kmpt";
 } else if  (c~/e/) {
    c="kmpt";
 } else if  (c~/f/) {
    c="undefined f";
 } else if  (c~/g/) {
    c="undefined g";
 } else if  (c~/h/) {
    c="undefined h";
 }
 if (length(trim(c))>0) {
   print "\""s"\", "c;
 }
}
}

function trim(v) {
    ## Remove leading and trailing spaces (add tabs if you like)
    sub(/^ */,"",v)
    sub(/ *$/,"",v)
    return v
 } 
