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
import java.util.*

/*
 * Chatter is a simple language used to test magpie.  Specifically magpie accepts S-expressions
 * and performs term re-writing based on the S-expression given and the mappings provided.
 * Chatter is a language used so that S-expressions can be represented as strings and parsed into
 * Expressions or Patterns that are used by Magpie instead of having to create a Pattern or
 * Expression manually.
 *
 * The form of the language is:
 *   name := [^ ()]+
 *   args := "(" expression* ")"
 *   expression := name (args)?
 *   params := "(" name* ")"
 *   pattern := name (params)?
 */

fun String.toExpression() =
    ExpressionParser(this).expression()

fun String.toPattern() =
    PatternParser(this).pattern()

fun Expression.toChatter(): String {
    if (this.args.isEmpty()) {
        return this.name
    }

    val builder = StringBuilder()
    builder.append(this.name)
    builder.append('(')
    for (i in this.args.indices) {
        if (i != 0) {
            builder.append(' ')
        }
        builder.append(this.args[i].toChatter())
    }
    builder.append(')')
    return builder.toString()
}

fun Pattern.toChatter(): String {
    val builder = StringBuilder()
    builder.append(this.name)
    builder.append('(')
    for (i in this.params.indices) {
        if (i != 0) {
            builder.append(' ')
        }
        builder.append(this.params[i])
    }
    builder.append(')')
    return builder.toString()
}

private const val NON_NAME_CHARS = " ()"

private fun String.isName(): Boolean {
    for (i in this.indices) {
        if (NON_NAME_CHARS.indexOf(this[i]) != -1) {
            return false;
        }
    }
    return true
}

private fun lex(text: String): Queue<String> {
    val queue: Queue<String> = LinkedList()

    val tokenizer = StringTokenizer(text, " ()", true)
    while (tokenizer.hasMoreTokens()) {
        val tok = tokenizer.nextToken()
        if (tok.isNotBlank()) {
            queue.offer(tok)
        }
    }

    return queue
}

private class ExpressionParser(text: String) {
    private val queue = lex(text)

    fun expression() = Expression(
        name = name() ?: throw tokenException("a name"),
        args = args() ?: emptyList()
    )

    private fun name() =
        if (queue.isNotEmpty() && queue.peek().isName()) {
            queue.poll()
        } else {
            null
        }

    private fun args(): List<Expression>? {
        if (!has("(")) {
            return null
        }

        val args = mutableListOf<Expression>()

        expect("(")
        while (!has(")")) {
            args.add(expression())
        }
        expect(")")

        return args
    }

    private fun has(token: String) = queue.isNotEmpty() && queue.peek() == token

    private fun expect(token: String) =
        if (has(token)) {
            queue.poll()
        } else {
            throw tokenException(token)
        }

    private fun tokenException(expected: String) =
        RuntimeException(
            if (queue.isEmpty()) {
                "Expected $expected but found the end of the stream"
            } else {
                "Expected $expected but found ${queue.peek()}"
            }
        )
}

private class PatternParser(text: String) {
    private val queue = lex(text)

    fun pattern() = Pattern(
        name = name() ?: throw tokenException("a name"),
        params = params() ?: emptyList()
    )

    private fun name() =
        if (queue.isNotEmpty() && queue.peek().isName()) {
            queue.poll()
        } else {
            null
        }

    private fun params(): List<String>? {
        if (!has("(")) {
            return null
        }

        val params = mutableListOf<String>()

        expect("(")
        while (!has(")")) {
            params.add(name() ?: throw tokenException("a name"))
        }
        expect(")")

        return params
    }

    private fun has(token: String) = queue.isNotEmpty() && queue.peek() == token

    private fun expect(token: String) =
        if (has(token)) {
            queue.poll()
        } else {
            throw tokenException(token)
        }

    private fun tokenException(expected: String) =
        RuntimeException(
            if (queue.isEmpty()) {
                "Expected $expected but found the end of the stream"
            } else {
                "Expected $expected but found ${queue.peek()}"
            }
        )
}
