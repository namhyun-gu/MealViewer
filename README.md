<h1 align="center">MealViewer</h1>

<p align="center">
    <img alt="License" src="https://img.shields.io/badge/License-Apache%202.0-blue.svg"/>
</p>

<!-- <p align="center">
🚧 Under construction 🚧
</p> -->

## Preview

<p align="center">
    <img src="images/img_search.png" width="32%"/>
    <img src="images/img_main.png" width="32%"/>
</p>

## Architecture

MVVM (View - ViewModel - Model)

## Built with

- [Kotlin](https://kotlinlang.org/) based, [Coroutines](https://github.com/Kotlin/kotlinx.coroutines) + [Flow](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/) for asynchronous.
- Data Binding
- Jetpack
  - [Compose](https://developer.android.com/jetpack/compose) for build UI.
  - ViewModel
  - Navigation
  - Data Store
  - Paging
- [Dagger Hilt](https://dagger.dev/hilt/) for dependency injection.
- [Retrofit](https://github.com/square/retrofit) for networking.
- [Moshi](https://github.com/square/moshi) for parse JSON.

## License

```
Copyright 2021 Namhyun, Gu

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```