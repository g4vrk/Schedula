# Schedula

**Compact scheduling framework for Paper and Folia.**

[![Build](https://github.com/g4vrk/Schedula/actions/workflows/build.yml/badge.svg)](https://github.com/g4vrk/Schedula/actions/workflows/build.yml)
[![Java](https://img.shields.io/badge/Java-21+-orange)](.)
[![Paper](https://img.shields.io/badge/Paper-1.18.2+-blue)](.)
[![Folia](https://img.shields.io/badge/Folia-1.21.11+-blue)](.)
[![Version](https://img.shields.io/badge/version-1.1.0-informational)](.)
[![JitPack](https://jitpack.io/v/g4vrk/Schedula.svg)](https://jitpack.io/#g4vrk/Schedula)
[![License](https://img.shields.io/github/license/g4vrk/Schedula)](LICENSE)

---

## Что это

Schedula предоставляет единый API для планирования задач, скрывая различия между Bukkit и Folia scheduler'ами.

Поддерживаются:

* sync и async выполнение;
* задержка и повторение;
* entity и location scheduling;
* отмена через `Task`;
* автоматический выбор платформы.

> [!NOTE]
> Schedula — библиотека, а не самостоятельный плагин.

---

## Модули

| Модуль   | Назначение                       |
| -------- | -------------------------------- |
| `common` | Основной API                     |
| `bukkit` | Paper/Bukkit implementation      |
| `folia`  | Folia implementation             |
| `multi`  | Paper/Folia API с auto-detection |

Для multiplatform-плагинов используйте `multi`.

---

## Подключение через JitPack

```kotlin
repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.g4vrk.Schedula:multi:1.1.0")
}
```

Для отдельных платформ:

```kotlin
implementation("com.github.g4vrk.Schedula:bukkit:1.1.0")
implementation("com.github.g4vrk.Schedula:folia:1.1.0")
```

<details>
<summary><b>Maven</b></summary>

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.g4vrk.Schedula</groupId>
    <artifactId>multi</artifactId>
    <version>1.1.0</version>
</dependency>
```

</details>

---

## Использование

Создание scheduler:

```java
MultiplatformSchedulaAPI api = MultiplatformSchedulaAPI.builder()
        .autoDetectFactoryFor(plugin)
        .build();

Scheduler scheduler = api.createScheduler();
```

Обычная задача:

```java
scheduler.schedule(
        () -> doSomething(),
        TickSchedule.instant()
);
```

Повторение:

```java
scheduler.schedule(
        () -> doSomething(),
        TickSchedule.repeating(0, 20)
);
```

Async:

```java
scheduler.scheduleAsync(
        () -> loadData(),
        TickSchedule.delayed(20)
);
```

---

## Self-cancelling tasks

Когда нужен доступ к текущей задаче, используйте `Consumer<Task>`:

```java
scheduler.schedule(
        task -> {
            update();

            if (finished) {
                task.cancel();
            }
        },
        TickSchedule.repeating(0, 20)
);
```

`Task` позволяет:

```java
task.cancel();
task.isCancelled();
task.sync();
```

---

## TickSchedule

Поддерживаются как ticks, так и `Duration`:

```java
TickSchedule.instant();

TickSchedule.delayed(20);

TickSchedule.repeating(20, 20);

TickSchedule.fixedRate(20);
```

```java
TickSchedule.delayed(Duration.ofSeconds(2));

TickSchedule.repeating(
        Duration.ofSeconds(1),
        Duration.ofSeconds(5)
);

TickSchedule.fixedRate(
        Duration.ofMillis(500)
);
```

`Duration` преобразуется в Minecraft ticks.

---

## Entity & Location

```java
scheduler.scheduleEntity(
        entity,
        task -> {
            if (!entity.isValid()) {
                task.cancel();
            }
        },
        TickSchedule.repeating(0, 20)
);
```

```java
scheduler.scheduleLocation(
        location,
        () -> doSomething(),
        TickSchedule.delayed(20)
);
```

На Folia эти методы используют соответствующие region/entity schedulers.

---

## Factory

Реализации можно выбрать вручную:

```java
SchedulaAPI api = SchedulaAPI.builder()
        .factory(new PaperSchedulerFactory(plugin))
        .build();
```

```java
SchedulaAPI api = SchedulaAPI.builder()
        .factory(new FoliaSchedulerFactory(plugin))
        .build();
```

Или автоматически через `multi`:

```java
MultiplatformSchedulaAPI api = MultiplatformSchedulaAPI.builder()
        .autoDetectFactoryFor(plugin)
        .build();
```

---

## Сборка

Требуется Java 21+.

```bash
./gradlew build
```

Готовые JAR-файлы находятся в:

```text
common/build/libs
bukkit/build/libs
folia/build/libs
multi/build/libs
```

---

## Лицензия

Schedula распространяется под MIT — см. [LICENSE](LICENSE).
