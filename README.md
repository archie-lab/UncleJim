UncleJim ("**Un**modifiable **Coll**ections for **J**ava&trade; **Imm**utability") brings the following to Java:

* Type-safe versions of Clojure's immutable collections
* An immutable Transformation Builder, kind of like Clojure's sequence abstraction.
* A tiny, type-safe data definition mini-language of brief helper functions: `vec()`, `set()`, `map()`, and `tup()`, (like Clojure's vector `[]`, set `#{}`, and map `{}`).

#Examples
Create a vector (List) of integers and perform some operations on it.
The results of each operation show in comments to the right.
```java
vec(4, 5)                        //          4, 5
        .precat(vec(1, 2, 3))    // 1, 2, 3, 4, 5
        .concat(vec(6, 7, 8, 9)) // 1, 2, 3, 4, 5, 6, 7, 8, 9
        .filter(i -> i > 4)      //             5, 6, 7, 8, 9
        .map(i -> i - 2)         //       3, 4, 5, 6, 7
        .take(4)                 //       3, 4, 5, 6
        .drop(2)                 //             5, 6
```

More extensive examples are implemented as unit tests to ensure that they remain correct and current.

* [Comparison with Traditional Java and Java 8 Streams](src/test/java/org/organicdesign/fp/TradJavaStreamComparisonTest.java#L22) - UncleJim generally takes 1/2 to 1/3 as much code to accomplish the same thing as Traditional Java, or Java 8 Streams.

* [Pure-Jim usage examples](src/test/java/org/organicdesign/fp/UsageExampleTest.java#L34) - Three different ways of improving your Java code with UncleJim.

#Learn
[JimTrainer](https://github.com/GlenKPeterson/JimTrainer) contains a few short problem-sets for learning UncleJim 

Maven Dependency
================

```xml
<dependency>
        <groupId>org.organicdesign</groupId>
        <artifactId>UncleJim</artifactId>
        <version>0.10.10</version>
</dependency>
```

#API Highlights

* Type-safe versions of Clojure's [immutable collections](src/main/java/org/organicdesign/fp/collections) (classes start with the word "Persistent")

* [Immutable Transformation Builder](src/main/java/org/organicdesign/fp/xform/Transformable.java)
(implementation is in [Xform](src/main/java/org/organicdesign/fp/xform/Xform.java))

* [Transformation Endpoints](src/main/java/org/organicdesign/fp/xform/Realizable.java)

* [Data Description Mini-Language](src/main/java/org/organicdesign/fp/StaticImports.java)

* Simplified Java 8 [functional interfaces](src/main/java/org/organicdesign/fp/function) that wrap checked exceptions

For complete API documentation, please build the javadoc:
`mvn javadoc:javadoc`

#Additional experimental features:
* An [Equator](src/main/java/org/organicdesign/fp/collections/Equator.java) and [ComparisonContext](src/main/java/org/organicdesign/fp/collections/Equator.java#L45) which work like `java.util.Comparator`, but for hash-based collections.
* [Memoization](src/main/java/org/organicdesign/fp/function/Function2.java#L59) for functions
* Unmodifiable interfaces which deprecate mutator methods and throw exceptions to retrofit legacy code and catch errors in your IDE instead of at runtime.
These were useful before the Clojure collections and Transformable were fully integrated, but may still provide a useful extension point for integrating your own immutable collections into the traditional Java ecosystem. 

#Build from Source

- Java 8 (tested with 64-bit Linux build 1.8.0_51).
- Maven (tested version: 3.19.0-26 64-bit Linux build)
- Maven will download jUnit for you
- First `mvn clean install` on: https://github.com/GlenKPeterson/TestUtils
- Then `mvn clean test` on UncleJim

#Project Status
**BETA** release.  The code quality is high, the documentation is improving, but there is still a chance of minor API changes before the final release. 
Test coverage at last check: 73%

![Test Coverage](testCoverage.png)

#Change Log
See [changeLog.txt](changeLog.txt)

#To Do
 - Update JavaDoc, esp. Im vs. Unmod
 - Bring unit test coverage back above 80%, or 85% if sensible.  This basically means to add any and all practical tests for PersistentHashMap, then remove unused code.
 - ?Make visio drawig of interface diagram?
 - Have an Ordered version of Transform as well as the (default) unreliable order.  Only the ordered version can be used for implementing things like equals() and hashCode()
 - Bring back the pointer-arithmetic version of drop()
 - Consider `max(Comparator<T> c, Iterable<? extends T> is)` and min()...
 - Add additional tuples and Functions up through 25 arguments
 - Study monadic thinking and ensure that Or is "monad-friendly".
 Ensure you can chain together functions in a short-circuiting way, without exceptions or other side-effects.
 - Add a [Persistent RRB Tree](http://infoscience.epfl.ch/record/169879/files/RMTrees.pdf) and compare its performance to the PersistentVector.
 - Re-implement Persistent collections from Algorithms and Purely Functional Data Structures without relying on a wrapped transient collection and without locking checks, then compare efficiency.

#Out of Scope

###Option<T> firstMatching(Predicate<T> pred);
Use with filter(...).head() instead

###T reduceLeft(BiFunction<T, T, T> fun)
reduceLeft() is like foldLeft without the "u" parameter.
I implemented it, but deleted it because it seemed like a very special case of foldLeft that only operated on items of the same type as the original collection.
I didn't think it improved readability or ease of use to have both methods.
How hard is it to pass a 0 or 1 to foldLeft?
It's easy enough to implement if there is a compelling use case where it's significantly better than foldLeft.
Otherwise, fewer methods means a simpler interface to learn.

###Transformable<T> forEach(Function1<? super T,?> consumer)
Java 8 has `void forEach(Consumer<? super T> action)` on both Iterable and Stream that does what
Transformable.forEach() used to do.  The old Transformable method overloaded (but did not override)
this method which is problematic for the reasons Josh Bloch gives in his Item 41.  Either make
use of the Java 8 `void forEach(i -> log(i))` or pass a constant function like
`i -> { print(i); return Boolean.TRUE; }` to
`Transformable<T> filter(Function1<? super T,Boolean> predicate)` instead. 

###Transformable<T> interpose(T item)
I also implemented interpose(), but took it out because my only use case was to add commas to a list to display
it in English and for that, you also need a conjunction, and often a continuation symbol:

a, b, c, or d.

a, b, c, and d.

a,b,c...

None of those are simple uses of interpose.

#Motivation

##Executive summary
To be able to write Java at work more like the way I write Clojure without taking any significant performance penalty for doing so.  Also, to be able to use the Clojure collections in a type-safe language.  I was thinking "Java" but really Scala can take advantage of the type safety improvements as well.

##Details
The goals of this project are to make it easy to use Java:

 - Immutably (Josh Bloch Item 15 and Clojure)
 - Type safely (Josh Bloch Item 23)
 - Functionally (using first-class functions more easily: Clojure and Scala)
 - Expressiveness/Brevity (Expressions over statements: all API calls evaluate to something useful for subsequent calls: Clojure and Scala).
 - Minimizing the use of primitives and arrays (except for varargs in 3 places, Suggested by Josh Bloch Items 23, 25, 26, 27, 28, 29, also Clojure and Scala)
 - Returning empty collections instead of <code>null</code> (Josh Bloch Item 43, also Clojure and Scala)
 - "Throw exceptions at people, not at code" (says Bill Venners, but also Josh Bloch Item 59)
 - Concurrency friendly (Josh Bloch Item 66, 67)
 - Context-sensitive equality: prefer Equator and Comparator to <code>equals()</code>, <code>hashcode()</code> and <code>compareTo()</code> ([Daniel Spiewak, Viktor Klang, Rúnar Óli Bjarnason, Hughes Chabot](http://glenpeterson.blogspot.com/2013/09/object-equality-is-context-relative.html), java.util.TreeSet, java.util.TreeMap)
 - Sensible toString() implementations (like Scala)
 - Compatibly with existing/legacy Java code

Higher order functions are not just briefer to write and read, they are less to *think* about.
They are useful abstractions that simplify code and focus your attention on your goals rather than the details of how to accomplish them.
Function chaining: <code>xs.map(x -> x + 1).filter(x -> x > 7).head()</code> defines what you are doing in the simplest possible way while hiding all details about how to iterate through the underlying collection.

The alternative - loops - are bundles of unnecessary complexity.
Loops generally require setting up accumulators, then running a gamut of <code>if</code>, <code>break</code>, and <code>continue</code> statements, like some kind of mad obstacle race that involves as many state changes as possible.
Different kinds of collections require different looping constructs - more complexity.
Looping code is vulnerable to "off-by-one" boundary overflow/underflow, improper initialization, accidental exit, infinite loops, forgetting to update a counter, updating the wrong counter...  The list goes on!
None of that has anything to do with why the loop was created in the first place which is to transform the underlying data.

You don't have to write that kind of code any more.
If you want to map one set of values according to a given function, say so with xs.map().
Filter?  xs.filter().
It's clearer, simpler, and like type safety, it eliminates whole classes of errors.

Clojure works like this, only the syntax makes the evaluation go inside out from the order you read the statements in (hence Clojure's two arrow operators).
With method chaining, the evaluation happens in the same order as the methods are written on the page, much like piping commands to one another in shell scripts.

The Xform class is the third one of its kind that I have written.  For a single thread, my timings show that its speed is comparable to a for loop.  In general, the overhead for using these transformations is minimal or non-existant.  In the cases where imutability does cause overhead (and there definitely are) it is generally well worth the clarity, safety, and productivity benefits it provides.
If you find a better/faster implementation, please submit your improvements!

Within your own FP-centric world, you will use the Im interfaces and implementations and transform them with the Transformation abstraction.  Methods that interact with imperative Java code will take the java.util interfaces and return either the Im- interfaces (or Un- interfaces) as necessary.

In Java, variables declared outside a lambda and used within one must be effectively finial.  The Mutable.Ref class works around this limitation.

#Thank You
The bulk of this project started as a simple question on StackExchange: [Why doesn't Java 8 include immutable collections?](http://programmers.stackexchange.com/questions/221762/why-doesnt-java-8-include-immutable-collections)  People's answers were a big help in figuring out what this project should and shouldn't do.

John Tollison: For a brief review and suggestions.

Nathan Williams: for many lengthy email conversations about this project, encouragement to separate state from the transformation, and occasional light code review.

GreenJUG: for bearing with talks on early versions of this code two years in a row.

Greenville Clojure (and Jeff Dik before that): for bearing with my newbie Clojure questions.

Mike Greata: for providing encouragement, advice, and patiently listening to me drone on about this as we carpooled to user group meetings.

Everyone whose ideas are collected in this project: I tried to put names in as close as possible to the contributions.

#Licenses
Java&trade; is a registered trademark of the Oracle Corporation in the US and other countries.
UncleJim is not part of Java.
Oracle is in no way affiliated with the UncleJim project.

UncleJim is not part of Clojure.
Rich Hickey and the Clojure team are in no way affiliated with the UncleJim project, though it borrows heavily from their thoughts and is partly a derivative work of their open-source code.

The Clojure collections are licensed under the Eclipse Public License.
Versions of them have been included in this project and modified to add type safety and implement different interfaces.
These files are still derivative works under the EPL.
The [EPL is not compatable with the GPL version 2 or 3](https://eclipse.org/legal/eplfaq.php#GPLCOMPATIBLE).
You can [add an exception to the GPL to allow you to release EPL code under this modified GPL](http://www.gnu.org/licenses/gpl-faq.html#GPLIncompatibleLibs), but not the other way around.

Thanks to Bodil Stokke for pointing out the EPL/GPL compatibility issue and work-around.

Unless otherwise stated, the rest of this work is licensed under the Apache 2.0 license.
New contributions should be made under the Apache 2.0 license whenever practical.
I believe it is more popular, clearer, and has been better tested in courts of law.
[The Apache 2.0 license is also one-way compatible with the GPL version 3](http://www.apache.org/licenses/GPL-compatibility.html), so that everything *except* the Clojure collections can be combined and re-distributed with GPLv3 code.
Apache is not compatible with GPLv2, though you might try the GPL modification mentioned in the previous paragraph.

As of 2015-03-24, the following statements made me think the Apache and EPL licenses were compatible enough for my purposes and for general enterprise adoption:

###From Apache
> For the purposes of being a dependency to an Apache product, which licenses
> are considered to be similar in terms to the Apache License 2.0?
>
> Works under the following licenses may be included within Apache products:
>
> ...
>
> Eclipse Distribution License 1.0
>
> ...
>
> Many of these licenses have specific attribution terms that need to be
> adhered to, for example CC-A, often by adding them to the NOTICE file. Ensure
> you are doing this when including these works. Note, this list is
> colloquially known as the Category A list.

Source (as of 2015-05-13): https://www.apache.org/legal/resolved#category-a

###From Eclipse
> What licenses are acceptable for third-party code redistributed by Eclipse
> projects?
>
> Eclipse views license compatibility through the lens of enabling successful
> commercial adoption of Eclipse technology in software products and services.
> We wish to create a commercial ecosystem based on the redistribution of
> Eclipse software technologies in commercially licensed software products.
> Determining whether a license for third-party code is acceptable often
> requires the input and advice of Eclipse’s legal advisors. If you have any
> questions, please contact license@eclipse.org.
>
> The current list of licenses approved for use by third-party code
> redistributed by Eclipse projects is:
>
> Apache Software License 1.1
>
> Apache Software License 2.0
>
> ...

Source (as of 2015-05-13): https://eclipse.org/legal/eplfaq.php#3RDPARTY
