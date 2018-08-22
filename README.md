# Бот для дискорда
Ориентирован на команду CS:GO, но подойдет также и для других целей

# Установка
Для работы большинства модулей бота потребуется база данных PostgreSQL.
Все пароли, логины, api-ключи и прочие конфиденциальные данные должны быть записаны в конфигурационном файле vacdbot.cfg.
Конфигурационный файл должен иметь вид:
Token=*****
SteamWebApiKey=*****
CsgotmApiKey=*****
DBDriver=org.postgresql.Driver
DBUrl=jdbc:postgresql://localhost:5432/salaleser
DBLogin=salaleser
DBPassword=password
VoiceRssApiKey=*****
