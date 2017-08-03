
WIP
======================
The API is going to change in versions 2.0-alpha+ until there will be stable 2.0 version.

For version 1.+ please check the [original repository](https://github.com/prolificinteractive/material-calendarview).

Calendar View 2
======================

[![Download](https://api.bintray.com/packages/ptrstovka/maven/calendarview2/images/download.svg) ](https://bintray.com/ptrstovka/maven/calendarview2/_latestVersion)[![Build Status](https://travis-ci.org/ptrstovka/calendarview2.svg?branch=master)](https://travis-ci.org/ptrstovka/calendarview2)

This is improved Material CalendarView from [Prolific Interactive](https://github.com/prolificinteractive/material-calendarview). My version 2+ has some breaking changes to the core, so the API is not backward compatible with prolific version.

<img src="/images/shot.png" alt="Demo" width="300px" />

Usage
-----
The artifact is available on jcenter.
1. Add library as dependency in your module build.gradle file.
```
compile 'com.ptrstovka.calendarview2:calendarview2:[latest version]'
```
2. You are done, now you can use `CalendarView2` in your code & layouts.

Example:

```xml
<com.ptrstovka.calendarview2.CalendarView2
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:mcv_weekDayTextAppearance="@style/CustomWeekTextAppearance"
        app:mcv_selectionColor="#4DB9C8"
        app:mcv_circlePadding="5dp"
        app:mcv_showOtherDates="all"
        />
```

Documentation
-------------

Make sure to check all the documentation available [here](docs/README.md).

Javadoc is available [here](https://ptrstovka.github.io/docs/calendarview2/).

More docs will be available on 2.0 release.

Customization
-------------

One of the aims of this library is to be customizable. The many options include:

* [Define the view's width and height in terms of tile size](docs/CUSTOMIZATION.md#tile-size)
* [Single or Multiple date selection, or disabling selection entirely](docs/CUSTOMIZATION.md#date-selection)
* [Showing dates from other months or those out of range](docs/CUSTOMIZATION.md#showing-other-dates)
* [Setting the first day of the week](docs/CUSTOMIZATION_BUILDER.md#first-day-of-the-week)
* [Show only a range of dates](docs/CUSTOMIZATION_BUILDER.md#date-ranges)
* [Customize the top bar](docs/CUSTOMIZATION.md#topbar-options)
* [Custom labels for the header, weekdays, or individual days](docs/CUSTOMIZATION.md#custom-labels)


### Events, Highlighting, Custom Selectors, and More!

All of this and more can be done via the decorator api. Please check out the [decorator documentation](docs/DECORATORS.md).

### Custom Selectors and Colors

If you provide custom drawables or colors, you'll want to make sure they respond to state.
Check out the [documentation for custom states](docs/CUSTOM_SELECTORS.md).

Contributing
============

Would you like to contribute? Fork us and send a pull request! Be sure to checkout our issues first.

## License

Material Calendar View is Copyright (c) 2017 Prolific Interactive. It may be redistributed under the terms specified in the [LICENSE] file.

[LICENSE]: /LICENSE

## Maintainers

Version 1.+ is maintained and funded by Prolific Interactive. The names and logos are trademarks of Prolific Interactive.
