BEGIN{i=0}
{
  if (i%2==0) {
    split ($0,a,";");split(a[2],b,":");
    s=trim(b[2]);
#    print "1: "s;
  } else {
    split ($0,a,";");
    c=trimColon(a[2]);
    print "\""s"\","c;
    if (length(a[3])>0) {
      c=trimColon(a[3]);
      print "\""s"\","c;
    }
    if (length(a[4])>0) {
      c=trimColon(a[4]);
      print "\""s"\","c;
    }
  }
  i++;
}

function trim(v) {
    ## Remove leading and trailing spaces (add tabs if you like)
    sub(/^ */,"",v)
    sub(/ *$/,"",v)
    return v
 } 
function trimColon(v) {
    ## Remove leading and trailing spaces (add tabs if you like)
    sub(/^\"*/,"",v)
    sub(/\"*$/,"",v)
    return v
 } 
