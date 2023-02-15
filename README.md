# NFCDemo-Android
### Host-based Card Emulation (HCE)

## What is the project about ?  :roll_eyes:
 - According to the android documentation here : https://developer.android.com/guide/topics/connectivity/nfc/hce, 
  we used this as a workround to make the Sender Device act as Card, while the Reader Device act as NFC reader
  ## Main Concepts :dizzy:
 - The device which acts as Card should implement an HCE service which is Service Component and provide implementaion for two methods **processCommandApdu** , **onDeactivated**
 - The device which acts as NFC reader implement **NfcAdapter.ReaderCallback** and enable Reader Mode in OnResume **nfcAdapter?.enableReaderMode(this, this,
            NfcAdapter.FLAG_READER_NFC_A or 
                    NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK,
            null)**

 ## Demo :dizzy:

![](https://media.giphy.com/media/v1.Y2lkPTc5MGI3NjExMjRlZDQ4MTc1NzlkYjIzNDU0OTcwOWIwYWU5ZDUxNGU5Y2QyMGI3YiZjdD1n/FXb9oxyHF7NPrLrdHe/giphy.gif)

[Demo Video](https://drive.google.com/file/d/1Ouq5VX0nOlLNsPxOfgkINeRhRwSvky7o/view?usp=share_link)

## Refrences : 
- For more information, please check this article too https://medium.com/the-almanac/how-to-build-a-simple-smart-card-emulator-reader-for-android-7975fae4040f
