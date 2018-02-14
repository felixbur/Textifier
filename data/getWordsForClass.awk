BEGIN{}
{
 rln=ln%6;

 if (rln==0) {
   word = $1;
 }
 if (rln==1) {
   a=$2;
   b=$3;
   c=$4;
   d=$5;
   e=$6;
   f=$7;
   if (a>=b&&a>=c&&a>=d&&a>=e&&a>=f) {
     aA[aI]=word;
     aI=aI+1;
   }
   if (b>=a&&b>=c&&b>=d&&b>=e&&b>=f) {
     bA[bI]=word;
     bI=bI+1;
   }
   if (c>=a&&c>=b&&c>=d&&c>=e&&c>=f) {
     cA[cI]=word;
     cI=cI+1;
   }
   if (d>=a&&d>=b&&d>=c&&d>=e&&d>=f) {
     dA[dI]=word;
     dI=dI+1;
   }
   if (e>=a&&e>=b&&e>=c&&e>=d&&e>=f) {
     eA[eI]=word;
     eI=eI+1;
   }
   if (f>=a&&f>=b&&f>=c&&f>=d&&f>=e) {
     fA[fI]=word;
     fI=fI+1;
   }
 }
 ln=ln+1;
}

END{
  print"";
  print "A: Fehler";
  for (i=0;i<aI;i++) {
    printf("%s, ",aA[i]);
  }
  print"";
  print "B: Kein gespräch";
  for (i=0;i<bI;i++) {
    printf("%s, ",bA[i]);
  }
  print"";
  print "C: Unterbrechung";
  for (i=0;i<cI;i++) {
    printf("%s, ",cA[i]);
  }
  print"";
  print "E: Kompetenz";
  for (i=0;i<dI;i++) {
    printf("%s, ",dA[i]);
  }
  print"";
  print "H: Wartezeit";
  for (i=0;i<eI;i++) {
    printf("%s, ",eA[i]);
  }
  print"";
  print "X: Not Applicable";
  for (i=0;i<fI;i++) {
    printf("%s, ",fA[i]);
  }
}

function max(a, b, c, d, e, f) {
  if (a>=b&&a>=c&&a>=d&&a>=e&&a>=f) {
    return "a";
  }
  if (b>=a&&b>=c&&b>=d&&b>=e&&b>=f) {
    return "b";
  }
  if (c>=a&&c>=b&&c>=d&&c>=e&&c>=f) {
    return "c";
  }
  if (d>=a&&d>=b&&d>=c&&d>=e&&d>=f) {
    return "d";
  }
  if (e>=a&&e>=b&&e>=c&&e>=d&&e>=f) {
    return "e";
  }
  if (f>=a&&f>=b&&f>=c&&f>=d&&f>=e) {
    return "f";
  }
}
