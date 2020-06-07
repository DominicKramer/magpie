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

fun main() {
    val map1 = Mapping(
        from = Pattern(
            name = "f",
            params = listOf("x")
        ),
        to = Expression(
            name = "g",
            args = listOf(
                Expression(
                    name = "x",
                    args = emptyList()
                )
            )
        )
    )

    val map2 = Mapping(
        from = Pattern(
            name = "g",
            params = listOf("x")
        ),
        to = Expression(
            name = "h",
            args = listOf(
                Expression(
                    name = "x",
                    args = emptyList()
                )
            )
        )
    )

    val map3 = Mapping(
        from = Pattern(
            name = "h",
            params = listOf("x")
        ),
        to = Expression(
            name = "f",
            args = listOf(
                Expression(
                    name = "x",
                    args = emptyList()
                )
            )
        )
    )

    val input0 = Expression(
        name = "f",
        args = listOf(
            Expression(
                name = "t",
                args = emptyList()
            )
        )
    )

    val input1 = Expression(
        name = "f",
        args = listOf(
            Expression(
                name = "X",
                args = listOf(
                    Expression(
                        name = "x",
                        args = emptyList()
                    ),
                    Expression(
                        name = "f",
                        args = listOf(
                            Expression(
                                name = "7",
                                args = emptyList()
                            )
                        )
                    )
                )
            )
        )
    )

    val input2 = Expression(
        name = "z",
        args = emptyList()
    )

    println(transform(input0, listOf()))
    println(transform(input0, listOf(map1)))
    println(transform(input0, listOf(map1, map2)))
    println(transform(input0, listOf(map1, map2, map3)))

    println(transform(input1, listOf(map1, map2, map3)))
    println(transform(input2, listOf(map1, map2, map3)))
}
