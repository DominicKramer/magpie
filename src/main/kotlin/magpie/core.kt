/*
 * Copyright 2020 Dominic Kramer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package magpie

import java.lang.RuntimeException

/**
 * Represents an expression of the form `f(x y z)` that supports nesting such as `f(x g(a b c) y f(x y) z)`.
 */
data class Expression(
    val name: String,
    val args: List<Expression>
)

/**
 * Represents a pattern of the form `f(x y z)`.  Note that patterns cannot be nested.
 */
data class Pattern(
    val name: String,
    val params: List<String>
)

/**
 * Represents a mapping from a pattern to an expression.  For example, `f(x y)` maps to `g(x h(y))`.
 */
data class Mapping(
    val from: Pattern,
    val to: Expression
)

fun transform(input: Expression, mappings: List<Mapping>): Expression {
    val mappingMap = mappings.map { getSignature(it.from) to it }.toMap()
    val expressionsEncountered = mutableSetOf<Expression>()
    var cur = input
    while (!expressionsEncountered.contains(cur)) {
        expressionsEncountered.add(cur)
        val next = transformOnce(cur, mappingMap)
        if (next == cur) {
            return next
        }
        cur = next
    }

    return cur
}

private fun transformOnce(input: Expression, mappingMap: Map<String, Mapping>): Expression {

    val transformedArgs = input.args.map {
        transformOnce(it, mappingMap)
    }

    val expSig = getSignature(expression = input)
    if (!mappingMap.containsKey(expSig)) {
        return Expression(
            name = input.name,
            args = transformedArgs
        )
    }

    val mapping = mappingMap[expSig]!!
    val argMap = mutableMapOf<String, Expression>()
    if (mapping.from.params.size != transformedArgs.size) {
        throw RuntimeException("Size mismatch")
    }

    for (i in mapping.from.params.indices) {
        argMap[mapping.from.params[i]] = transformedArgs[i]
    }

    return Expression(
        name = mapping.to.name,
        args = mapping.to.args.map {
            substitute(it, argMap)
        }
    )
}

private fun substitute(expression: Expression, varToValue: Map<String, Expression>): Expression {
    if (expression.args.isEmpty()) {
        return varToValue[expression.name] ?: expression
    }

    return Expression(
        name = expression.name,
        args = expression.args.map {
            substitute(it, varToValue)
        }
    )
}

private fun getSignature(pattern: Pattern) = "${pattern.name}#${pattern.params.size}"
private fun getSignature(expression: Expression) = "${expression.name}#${expression.args.size}"
