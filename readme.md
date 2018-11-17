# Bot

A bot (maybe? I guess) to run on Raspberry Pis.

## Build

Build with
```
sudo ./mvnw spring-boot:run
```

Note that sudo is needed for gpio manipulation.

## Dependencies

This project requires wiringpi 2.46 or later:
```
cd /tmp
wget https://unicorn.drogon.net/wiringpi-2.46-1.deb
sudo dpkg -i wiringpi-2.46-1.deb
```


## Licence

Licenced under MIT.

The PID controller is inspired heavily by the [Arduino PID Library](https://github.com/br3ttb/Arduino-PID-Library).
