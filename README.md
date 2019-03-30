# EditTextMasked
A library to put masks on EditTexts and in any String.  
Support multiple mask on the same EditText.  

## Installation

```
    allprojects {
        repositories {
	    ...
	    maven { url 'https://jitpack.io' }
	}
    }
```

```
    dependencies {
        implementation 'com.github.tokenlab:EditTextMasked:VERSION'
    }
```

## How to use

```
    editTextDocument.setMask("###.###.###-##")
    editTextMultipleDocuments.setMasks(listOf("###.###.###-##", "##.###.###/####-##"))
    editTextMoney.addCurrencyMask(Locale("pt", "BR))

    val someDate = "13032019"
    textViewDate.text = someDate.setMask("##/##/####")
```

### Mask format
For default, the replaceable symbol is "#", but you can change it whenever you want like this:
```
    editTextPhone.setMask(mask = "(**) *****-****", replaceableSymbol = '*')
```

### Get text without any mask
```
    formatedString.getRawText()
```

## Author
[ludmilamm](https://github.com/ludmilamm)
