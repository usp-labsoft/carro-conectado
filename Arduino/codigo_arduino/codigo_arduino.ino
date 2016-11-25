int i = 0;
String stringToWrite = "";
int speeds[] = {3,12,24,14,15,21,19,5,0,0,0,0,7,27,44,36,41,32,25,22,22,26,28,36,39,41,35,41,37,30,38,38,25,10,10,0,0,0,0,0,8,23,33,40,42,31,34,30,40,40,34,22,6,0,0,0,4,5,0,3,8,0,0,3,10,25,27,15,4,0};

void setup() {
  Serial.begin(9600);
  while (!Serial) {;}
  stringToWrite.reserve(200);
}

void loop() { // run over and over
  if (Serial) {
    stringToWrite = String(speeds[i]) + "#";
//    Serial.print(stringToWrite)
    for (int j = 0; j < stringToWrite.length(); j++)
    {
      Serial.write(stringToWrite[j]);   // Push each char 1 by 1 on each loop pass
    }
    Serial.write(sizeof(speeds)/ sizeof(int));
    i++;
    if(i >= sizeof(speeds)/ sizeof(int)){i = 0;}
    delay(5*1000);
  }
}
