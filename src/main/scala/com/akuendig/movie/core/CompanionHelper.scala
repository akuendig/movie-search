package com.akuendig.movie.core

/**
 * Created with IntelliJ IDEA.
 * User: adrian
 * Date: 17.08.13
 * Time: 13:17
 * To change this template use File | Settings | File Templates.
 */
object CompanionHelper {

  def companion(clazz: Class[_]): AnyRef = {
    // runtime reflection is typically done
    // by importing things from scala.reflect.runtime package
    import scala.reflect.runtime._

    // the new Scala reflection API is mirror based
    // mirrors constitute a hierarchy of objects
    // that closely follows the hierarchy of the things they reflect
    // for example, for a class you'll have a ClassMirror
    // for a method you'll have a MethodMirror and so on
    // why go the extra mile?
    // because this provides more flexibility than traditional approaches
    // you can read more about mirror-based designs here:
    // https://dl.dropbox.com/u/10497693/Library/Computer%20Science/Metaprogramming/Reflection/mirrors.pdf
    // https://dl.dropbox.com/u/10497693/Library/Computer%20Science/Metaprogramming/Reflection/reflecting-scala.pdf

    // bottom line is that to do anything you will need a mirror
    // for example, in your case, you need a ClassMirror

    // remember I said that mirrors provide more flexibility?
    // for one, this means that mirror-based reflection facilities
    // might have multiple implementations
    // in a paper linked above, Gilad Bracha muses over a runtime
    // that loads things remotely over the network
    // in our case we might have different mirrors for JVM and CLR
    // well, anyways

    // the canonical (and the only one now) implementation of the mirror API
    // is Java-based reflection that uses out of the box classloaders
    // here's its root: https://github.com/scalamacros/kepler/blob/9f71e9f114c10b52350c6c4ec757159f06e55daa/src/reflect/scala/reflect/api/Mirrors.scala#L178
    // yeah, right, I've just linked a source file from trunk
    // we'll have Scaladocs for that soon, but for now take a look
    // this file is interfaces-only and is heavy on comments

    // to start with Java-based reflection implementation you need a classloader
    // let's grab one and instantiate the root mirror
    // btw, the same effect could be achieved by writing
    // `scala.reflect.runtime.currentMirror`
    val rootMirror = universe.runtimeMirror(clazz.getClassLoader)

    // now when we've finally entered the reflective world
    // we can get the stuff done
    // first we obtain a ClassSymbol that corresponds to the current instance
    // (ClassSymbols are to Scala the same as Classes are to Java)
    val classSymbol = rootMirror.classSymbol(clazz)

    // having a Scala reflection entity
    // we can obtain its reflection using the rootMirror
    val moduleSymbol = classSymbol.companionSymbol.asModule

    // now we just traverse the conceptual hierarchy of mirrors
    // that closely follows the hierarchy of Scala reflection concepts
    // for example, a ClassMirror has a companion ModuleMirror and vice versa
    val moduleMirror = rootMirror.reflectModule(moduleSymbol)

    // finally, we've arrived at our destination
    moduleMirror.instance.asInstanceOf[AnyRef]
  }
}
