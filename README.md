# Web3 Provider

Wallet service is a part of the AMPnet crowdfunding project.

## Start

Application is running on port: `8135`. To change default port set configuration: `server.port`.

### Build

```sh
./gradlew build
```

### Run

```sh
./gradlew bootRun
```

After starting the application, API documentation is available at: `localhost:8135/docs/index.html`.
If documentation is missing generate it by running gradle task:

```sh
./gradlew copyDocs
```

### Test

```sh
./gradlew test
```

## Application Properties

### Provider

Set web3 provider api through: `com.ampnet.web3provider.provider.blockchain-api`
