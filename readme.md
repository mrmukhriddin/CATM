# CATM.Android

## Технологический стек

- Язык: Kotlin ☑
- Архитектура: [MVVM](https://developer.android.com/jetpack/guide) ☑
    + https://www.toptal.com/android/android-apps-mvvm-with-clean-architecture
- Хранение данных: [Room](https://developer.android.com/training/data-storage/room) ☑
- Сеть: [retrofit](https://square.github.io/retrofit/) ☑
    + [OkHttp](https://square.github.io/okhttp/)
- Асинхронная работа: [RxJava 3](https://github.com/ReactiveX/RxJava) ☑
- Изображения: [Glide](https://github.com/bumptech/glide) ☑
- Сериализация: [KotlinX Serialization](https://github.com/Kotlin/kotlinx.serialization) ☑
- DI: [Hilt](https://dagger.dev/hilt/) (based on [Dagger](https://dagger.dev/)) ☑

## Вспомогательные библиотеки

- Сканирование QR: [Ml-kit](https://developers.google.com/ml-kit/vision/barcode-scanning/android) ☑
- Навигация: [Cicerone](https://github.com/terrakok/Cicerone) ☑
# CATM.Android

## Оглавление
- Описание технологических процессов
    - Принцип добавления новых экранов и/или функционала
    - Архитектура - MVVM
    - Навигация - Cicerone
    - Инъекция зависимостей - Hilt
    - База данных - Room
    - Работа с сетью - Retrofit, OkHttp
    - Работа с потоками - RxJava3
- Экраны
- Дополнительно
    - Где менять url
    - FeatureToggle
    - Экран создания наряд-допуска

## Какие технологии были использованы
### Принцип добавления новых экранов и/или функционала
В приложении используется многомодульная система, которая позволяет поделить зоны ответственности каждого отдельного компонента. Все модули находятся в папке `sources`. Модули делятся на `:feature-...` и `:core-...`.
- `:feature-...` - это модули, которые содержат в себе какой-либо функционал. Сканер QR-кодов, профиль, главный экран или бизнес-логика - каждая из этих сущностей заслуживает отдельного модуля.
    - `:feature-...-ui` - модули, которые содержат ui-отображение. В них у нас лежат вью модели, активности, di-модули для них, адаптеры, делегаты и так далее.
    - `:feature-...` - модули, которые содержат бизнес-логику. В них у нас лежат юзкейсы, репозитории, сервисы и так далее.
- `:core-...` - это модули, которые содержат в себе что-то фундаментальное, что-то такое, что может быть переиспользовано во многих местах. Например: `:core-ui` содержит в себе ресурсы, стили, темы, цвета и прочая; `:core-navigation` содержит все экраны и классы, которые нужны для базовой работы навигации.

Соответственно, если хотим создать что-то новое, что не задевает модулей, которые уже существуют, то нужно создать для этого отдельный модуль со своим di-модулем (об этом подробнее в разделе инъекций зависимостей)  
  
ВАЖНО! : При создании новых ui-модулей, которые будут содержать в себе активности, наследующиеся от `BaseActivity`, мы должны в `build.gradle` модуля имплементировать `:core-navigation` и `:core-license`. Иначе будет ошибка ui при инжекте зависимостей  

### Архитектура - MVVM
В приложении используется архитектура MVVM. Для того, чтобы создать новую MVVM активность, мы должны пометить ее аннотацией `@AndroidEntryPoint`, сделать ее наследником `BaseActivity` и создать в ней поле `viewModel`, для будущих интерекаций со вьюмоделью, и поле `binding` для интеракций с разметкой вашей активности.
Во вьюмоделе проставляем перед классом аннотацию `@HiltViewModel` для корректной работы хилта. Сам класс должен быть наследником от `ViewModel` и иметь `@Inject` конструктор.

Примеры активности со вьюмоделью прикладываю:

```Kotlin
@AndroidEntryPoint
internal class ExampleActivity : BaseActivity() {

    private val viewModel: ExampleViewModel by viewModels()
    // CreateMethod.INFLATE важно проставить в аргументы делегата
    private val binding: ActivityExampleBinding by viewBinding(CreateMethod.INFLATE) 
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupUi()
        setupToolbar()
        setupObservers()
    }
    
    private fun setupUi() {
        // проставляем всевозможные слушатели нажатий и подобное
    }
    
    private fun setupToolbar() {
        // проставляем наш тулбар, если активности он нужен
        setToolbar(
            binding.toolbarContainer.toolbar,
            showTitle = true, // false, если не хотим показывать тулбар
            title = title.toString(), // название, отображаемое в тулбаре, должно быть прописано в манифесте
            showNavigate = true, // false, если не хотим показывать стрелку назад
        )
    }
    
    private fun setupObservers() {
        viewModel.observable.observe(this) { observableValue ->
            // тут уже при получении значения совершаем соответствующие действия
        }
    }

    override fun onBackPressed() {
        // всю навигацию (с редкими исключениями) производим во вью модели. в том числе и выход с экрана
        viewModel.exit()
    }
}
```

```Kotlin
@HiltViewModel
class ExampleViewModel @Inject constructor(
    private val appRouter: Router,
) : ViewModel() {

    // делим LiveData на два поля - одно для внутреннего пользования, изменяемое, а другое наружу, иммутабельное
    private val _observable: MutableLiveData<YourType> = MutableLiveData()
    val observable: LiveData<YourType> = _observable

    fun exit() {
        // вся навигацию: переходы к экранам, выход с экрана, завершение цепочек, производим через глобальный роутер (подробнее в разделе 'Навигация')
        appRouter.exit()
    }
}
```

### Навигация - Cicerone
Навигация работает через библиотеку Cicerone. Если вкратце, то мы уходим от концепта интентов и внутренних фрагментов и заменяем все экранами.
Если у нас есть необходимость создать новый экран и дать возможность на него переходить, мы в модуле `:core-navigation` в пакете `screens` создаем нужный нам экран.
**Пример создания экранов:**
1. Создание экрана активности с двумя аргументами
```Kotlin
fun interface ExampleScreen {
    
    operator fun invoke(ourArgumentOne: String, ourArgumentTwo: YourType): ActivityScreen
}
```

2. Создание экрана активности с ожиданием результата:
```Kotlin
fun interface ExampleScreenForResult {

    operator fun invoke(): ResultScreen

    companion object {

        const val KEY = "key.example"
    }
}
```
**P. S.**: `ResultScreen` - это наследник `ActivityScreen` с полем `resultKey`, нужно для последующей передачи результата

Когда мы создали экран в `:core-navigation`, нужно сделать его реализацю (а точнее провайд) в модуле, к которому принадлежит экран.
Например, у нас свой модуль `:feature-example-ui`. У него есть в пакете `di.module` класс модуля Хилта - `ExampleModuleUi`
```Kotlin
@Module
@InstallIn(SingletonComponent::class)
internal abstract class ExampleUiModule {

    companion object {
    
        @Provides
        fun provideExampleScreen(): ExampleScreen =
            ExampleScreen { argumentOne -> // тут может быть как больше аргументов, так и вовсе не быть
                object : ResultScreen {
                
                    // перегружаем resultKey с помощью ключа нашего экрана
                    override val resultKey: String = ExampleScreen.KEY 

                    // перегружаем метод, который вернет Intent на нужную нам активность
                    override fun createIntent(context: Context): Intent {
                        return ExampleScreen.createIntent(context, argumentOne)
                    }
                }
            }
    }
}
```
**P. S.**: `ActivityScreen` и `FragmentScreen` провайдятся таким же образом. Можно посмотреть примеры в коде, например, в `AboutAppUiModule` для `ActivityScreen` или `BriefingsUiModule` для `FragmentScreen`

После этого всего мы можем инжектить экраны в нужные нам места и переходить на них с помощью `appRouter`, о котором будет сказано далее.

У нас есть два случая навигации:
1. Мы осуществляем переход от активности к активности
   В этом случае мы пользуемся `ApplicationRouter`, который инжектится во вьюмодель.
   Примеры использования можно посмотреть в активностях, однако пару приведу и здесь:
    - `appRouter.exit()` - осуществляет выход из актвиности на предыдущую активность
    - `appRouter.navigateTo(screen())` - переход от данной активности на другую активность (экран)
    - `appRouter.newRootScreen(authScreen())` - переход от данной активности на другую активность (экран) с установлением новой активности корневой в стеке
    - `appRouter.navigateToWithResult<Boolean>(briefingQuizScreen(briefingId)) { ... }` - переход от данной активности на другую активность (экран) с возможным получением результата
        - в данном случае на последующем экране мы должны будем вызвать метод `appRouter.sendResultBy(Screen.KEY, valueToSend)`, в котором `Screen.KEY` - это уникальная константа, которая определяет экран, а `valueToSend` - значение, которое мы хотим передать экрану, который нас вызвал
    - `appRouter.replaceScreen(profileScreen(id))` - замена данной активности на другую активность (экран)

2. Мы осуществляем переход внутри активности
   Тут все немного сложнее. Для каждого отдельного случая нужно будет создавать свой отдельный `Router`, для которого нам нужно будет также создавать свой `Cicerone` и `Navigator`.
   Пример использования можно посмотреть в `BriefingMainFragment` (здесь у нас инициализируются сами объекты `Cicerone`, `Navigator` и `Router`) и в `BriefingMainViewModel` (здесь сами примеры навигации)

### Инъекция зависимостей - Hilt
Для работы с инъекцией зависимостей используем надстройку над даггером - Hilt. С ним все делается в пару строчек (почти буквально). При создании нового модуля, мы создаем в нем пакет `di.module` и там создаем наш di-модуль.
Именно в этом модуле мы будем предостовлять провайды и байнды. Примеры данных модулей можно посмотреть в коде, например `BriefingsUiModule`.
Перед нашим модулем должна стоять аннотации `@Module` и `@InstallIn(TargetComponent::class)`, где `TargetComponent` - это нужный нам компонент.
Пример созданного модуля:
```Kotlin
@Module
@InstallIn(SingletonComponent::class)
internal abstract class ExampleModule private constructor() {
    ...
}
```

### База данных - Room
База данных в проекте уже создана - `CatmDatabase`. При тестировании и изменении в схеме - повышаем версии.
Если хотим создать новую Dao, то:
1. Создаем Dao в модуле, который нам нужен. Обычно это модуль бизнес-логики и пакет `db`
   Например, в модуле `:feature-example` создаем в пакете `db` - `ExampleDao`
```Kotlin
@Dao
interface ExampleDao {

    // наши запросы
}
```
2. Добавляем в `CatmDatabase` новый Dao
```Kotlin
...
internal abstract class CatmDatabase : RoomDatabase() {

    abstract fun exampleDao(): ExampleDao
}
```
3. Добавить провайд в `RoomModule`
```Kotlin
@Module
@InstallIn(SingletonComponent::class)
internal abstract class RoomModule private constructor() {
    
    companion object {
        ...
        
        @Provides
        fun provideExampleDao(db: CatmDatabase): ExampleDao = db.exampleDao()
    }
}
```
**P.S.**: стараемся создавать отдельные сущности для бд с суффиксом `Db`

### Работа с сетью - Retrofit, OkHttp
В модуле с бизнес-логикой в пакете `services` мы создаем интерфейс, например, `ExampleApi`, в котором пишем ретрофит запросы.
Остальное все настроено. Если нужно будет добавить новый интерсептор запросов, провайдер, хедер или что-либо еще - смотри модуль `:core-network`

В di-модуле мы создаем провайд для нашего нового сервиса.
```Kotlin
@Module
@InstallIn(SingletonComponent::class)
internal abstract class ExampleModule private constructor() {
    ...
    
    companion object {
        
        @Provides
        fun provideExampleApi(client: ApiClient): ExampleApi =
            client.createService(ExampleApi::class.java)
    }
}
```

В модулях с бизнес-логикой мы используем `UseCase`

### Работа с потоками - RxJava3
Тут ничего необычного нет, обычная работа с RxJava3. Корутины не используются в проекте.

## Экраны
- Сплэш
  Экран входа в приложение
    - `SplashActivity`
- Логин
  Экран логина в приложение
    - `LoginActivity`
- О приложении
  Экран с информацией о приложении
    - `AboutAppActivity`
- Главный экран
  Экран, на котором находится несколько подэкранов, между которыми можно переключаться с помощью нижнего меню
    - `CoreActivity`
        - `MainFragment`
          Экран, на котором изображена информация о пользователе, qr-код
        - `BriefingsMainFragment`
          Экран, на котором расположены инструктажи
            - `BriefingDetailsActivity`
              Экран с деталями инструктажа
            - `BriefingQuizActivity`
              Экран с тестом для инструктажа
        - `EducationFragment`
          Экран с переходом на сайт по обучению
        - `DocumentsFragment`
          Экран с документами
        - `FeedbackFragment`
          Экран, на котором можно отправить обратную связь
- Синхронизация
  Экран для offline-синхронизации данных
    - `SyncActivity`
- Сканирование qr-кода
  Экран, на котором расположен qr-сканнер
    - `QrScannerActivity`
- Профиль
  Экран для просмотра профиля
    - `ProfileActivity`
- Уведомления
  Экран для просмотра пришедших уведомлений
    - `NotificationsActivity`
- Рабочие
  Экран для просмотра рабочих
    - `WorkersActivity` в модуле `:feature-profile-ui`
- Медиа (изображения, pdf-файлы)
  Экран для просмотра медиа
    - `MediaPreviewActivity`
- Список наряд-допусков
  Экран для просмотра всех наряд-допусков разных статусов
    - `WorkPermitsActivity`
- Фильтры для наряд-допусков
  Экран для выбора фильтров наряд-допусков
    - `WorkPermitsFilterActivity`
- Создание наряд-допуска
  Экран для создания нового наряд-допуска
    - `CreateDailyPermitsActivity`
- Детали наряд-допуска
  Экран деталей наряд-допуска
    - `WorkPermitDetailsActivity`
        - `AdditionalDocumentsActivity`
          Экран с отображением дополнительных документов
        - `CreateGasAirAnalysisActivity`
          Экран для создания нового газовоздушного анализа
        - `DailyPermitsActivity`
          Экран с отображением ежедневных наряд-допусков
        - `EndDailyPermitActivity`
          Экран для завершения ежедневного наряд-допуска
        - `ExtendWorkPermitActivity`
          Экран для продления наряд-допуска
        - `GasAirAnalyzesActivity`
          Экран с отображением газовоздушных анализов
        - `RiskFactorsActivity`
          Экран с отображением опасных факторов
        - `SignersActivity`
          Экран с отображением подписующих
        - `WorkersActivity` в модуле `:feature-work-permit-ui`
          Экран с отображением рабочих
- Переход в оффлайн режим
  Опциональный экран, появляется поверх всех экранов, чтобы предложить пользователю перейти в оффлайн режим
    - `OfflineSwitcherActivity`

## Дополнительно

### Где менять url
Url в проекте меняется в файле `configuration.xml`. По умолчанию в релизной версии приложения будет использоваться поле с названием `base_url` (!!!), поэтому менять нужно именно её

### FeatureToggle
Некоторые фишки (особенно для тестирования) удобно оставлять в коде, но при этом делать их включаемыми и выключаемыми. В этом нам помогает `FeatureToggle`. В релизе никаких тестовых вещей не включено, однако в дебаге есть такие удобные вещи, как сразу 4 типа роли на вход. Для кастомизации разработчиком.

### Экран создания наряд-допуска
Для создания наряд-допуска была создана отдельная система преобразования сущностей в UI и получения данных из них. Разделю её описание на два блока. Первый - описание сущности `StepPatternLayout`. Второй - описание сущности `ProcessManager`. Обе они находятся в модуле `:feature-step`

**`StepPatternLayout`**
Это вью, которая позволяет создавать графический интерфейс формы из сущностей наследников `Field`. Например, у нас бывают наследники такие: `Text` - для формы одно- или многострочного текста; `DoubleText` - для формы двойного текстового поля; `Check` - для формы галки; `DateAndTime` - для формы даты и времени и т. д.
Эти формы можно добавлять самими, в зависимости от нужд. Для каждого уникального `Field` существует уникальный `PatternDataType`. Внутри `Field` должен быть записан наследник `BaseInput`. Этот наследник будет содержать в себе все данные, нужные для создания формы. Пример:
```Kotlin
// Field - это sealed класс. Нам это понадобится дальше, когда мы будем собирать данные с итоговой формы
sealed class Field<T>(
    val tag: String,
    val input: BaseInput<T>?
) {

    // тут мы создаем текстовое поле
    class Text(tag: String, input: Input) : Field<String>(tag, input) {

        override val type: PatternDataType = PatternDataType.TEXT

        // для создания текстового поля нам нужны:
        class Input(
            val label: StringResWrapper,  // заголовок
            val hint: StringResWrapper, // подсказака
            override val initialValue: String? = null, // изначальное значение (перегружено), его тип указан ниже в BaseInput
            val oneLine: Boolean = true, // однострочное ли поле
            val isEditable: Boolean = true, // можно ли его редактировать
            val clarification: StringResWrapper? = null, // пояснение
        ) : BaseInput<String>(initialValue)
    }
}
```

Далее, имея все данные для создания поля, мы можем написать фабрику, которая нам бы по этой сущности действительно бы создавала UI-форму. Для этого нам понадобится создать наследника `StepFactory`. Для поля `Text` - это `TextFactory`. Пример можно посмотреть в коде, а сейчас пройдусь по наиболее важным моментам.
У класса фабрики шага есть один метод для перегрузки - это метод создания вью. Мы можем его не перегружать (данный пример можно посомтреть в `ProgressFactory`), а просто передать родителя, айди разметки и способ байндинга (называется `binder` в `StepFactory`), и тогда он нам просто создаст вью. Перегружать данный метод нужно только в том случае, если мы хотим проставить собственные данные (то есть, почти всегда).
По итогу метод `createView(...)` должен нам вернуть `View`

У нас создана фабрика для наших полей и мы можем теперь создавать формы. Осталось только решить вопрос того, как получать данные из формы. Для этого нам понадобится создать наследника `StepManager`. Он отвечает за несколько вещей: получение данных для восстановления внешнего вида вью, получение данных, необходимых для бизнес-логики, очистку формы, слушание формы, отображение ошибок (опционально) и проставление значений мануально (опционально).
Пример наследника - `TextManager`.

Потом, всевозможные фабрики и менеджеры, которые мы создали, передаем в `StepPatternLayout`, который обладает удобными методами получения данных (как для восстановления, так и для бизнес-логики), валидации полноты формы (`statusListener`), получение и проставления поля по тэгу и т. д.

Также для форм есть валидаторы, и это тоже предусмотрено. Пример добавления валидаторов можно посмотреть во фрагменте `GeneralInfoFragment:215`
Чтобы создать свой валидатор - вам нужно создать наследника `Validator`. Примеры валидаторов - `DateValidator`, `DateAfterTodayValidator` и другие

**P.S.:** Данная вьюгруппа используется не только для создания наряд-допуска, но и для создания форм на других экранах. Пример - `CreateGasAirAnalysisActivity`

**`ProcessManager`**
Это инструмент для организации пошагового процесса сбора формы с сохранениями информации между шагами. Для создания наряд-допуска был создан наследник `CreateWorkPermitProcessManager`, который переопределяет способ создания фрагментов на каждом из этапов. Каждый этап - это отдельный фрагмент, с которого можно собрать любую информацию, какую захотим. 
