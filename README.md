# Alarm Reporter

This app sends an HTTP request whenever you set your alarm. It relies on Android's built-in alarm system, so it should be compatbible with most/all alarm apps.

You can configure the URL the request is sent to. The request is always a POST request with one parameter: "timestamp". The value is the timestamp in milliseconds of the first upcoming alarm. If there's no alarm set, it'll be 0.

## Example receiver

```php
<?php
        if($_POST['timestamp'] != '0' && date('H', $_POST['timestamp'] / 1000) >= 12) {
                exit("Waking up late? ;)");
        }
	exit("OK");
?>
```
