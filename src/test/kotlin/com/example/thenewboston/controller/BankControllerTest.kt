package com.example.thenewboston.controller

import com.example.thenewboston.model.Bank
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.servlet.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get

@SpringBootTest
@AutoConfigureMockMvc
internal class BankControllerTest @Autowired constructor(
    val mockMvc: MockMvc,
    val objectMapper: ObjectMapper
) {


    val baseUrl = "/api/banks"

    @Nested
    @DisplayName("GET /api/banks")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class GetBanks {


        @Test
        fun `should return all banks`() {
            // when then
            mockMvc.perform(get(baseUrl))
                .andDo(print()) // Correct usage of print() here
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].accountNumber").value("1234"))
        }
    }


    @Nested
    @DisplayName("GET /api/banks/{accountNumber}")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class GetBank {
        @Test
        fun `should return the bank with the given number`() {
            //given

            val accountNumber = 1234

            //when then

            mockMvc.get("$baseUrl/$accountNumber")
                .andDo { print() }
                .andExpect { status { isOk() } }
                .andExpect { content { contentType(MediaType.APPLICATION_JSON) } }
                .andExpect { jsonPath("$.trust") { value("3.14") } }
                .andExpect { jsonPath("$.transactionFee") { value("17") } }

        }
    }

    @Test
    fun `should return not found if number doesnt exist`() {
        //given

        val accountNumber = "doesnt exist"

        //when
        mockMvc.get("$baseUrl/$accountNumber")
            .andDo { print() }
            .andExpect { status { isNotFound() } }


        //then

    }

    @Nested
    @DisplayName("POST /api/banks")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class PostNewBank {


        @Test
        fun `should add the new bank`() {


            //given
            val newBank = Bank("acc123", 31.415, 2)


            //when
            val performPost = mockMvc.post(baseUrl) {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(newBank)
            }


            // then
            performPost
                .andDo { print() }
                .andExpect {
                    status { isCreated() }
                    content {
                        contentType(MediaType.APPLICATION_JSON)
                        json(objectMapper.writeValueAsString(newBank))
                    }
                }

            @Test
            fun `should return BAD REQUEST if bank with given account number already exists`() {
                // given
                val invalidBank = Bank("1234", 1.0, 1)

                // when
                val performPost = mockMvc.post(baseUrl) {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(invalidBank)
                }

                // then
                performPost
                    .andDo { print() }
                    .andExpect { status { isBadRequest() } }
            }

        }

        @Nested
        @DisplayName("PATCH /api/banks")
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        inner class PatchExistingBank {

            @Test
            fun `should update an existing bank`() {
                // given
                val updatedBank = Bank("1234", 1.0, 1)

                // when
                val performPatchRequest = mockMvc.patch(baseUrl) {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(updatedBank)
                }

                // then
                performPatchRequest
                    .andDo { print() }
                    .andExpect {
                        status { isOk() }
                        content {
                            contentType(MediaType.APPLICATION_JSON)
                            json(objectMapper.writeValueAsString(updatedBank))
                        }
                    }

                mockMvc.get("$baseUrl/${updatedBank.accountNumber}")
                    .andExpect { content { json(objectMapper.writeValueAsString(updatedBank)) } }
            }

            @Test
            fun `should return BAD REQUEST if no bank with given account number exists`() {
                // given
                val invalidBank = Bank("does_not_exist", 1.0, 1)

                // when
                val performPatchRequest = mockMvc.patch(baseUrl) {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(invalidBank)
                }

                // then
                performPatchRequest
                    .andDo { print() }
                    .andExpect { status { isNotFound() } }
            }
        }

        @Nested
        @DisplayName("DELETE /api/banks/{accountNumber}")
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        inner class DeleteExistingBank {

            @Test
            @DirtiesContext
            fun `should delete the bank with the given account number`() {
                // given
                val accountNumber = 1234

                // when/then
                mockMvc.delete("$baseUrl/$accountNumber")
                    .andDo { print() }
                    .andExpect {
                        status { isNoContent() }
                    }

                mockMvc.get("$baseUrl/$accountNumber")
                    .andExpect { status { isNotFound() } }
            }

            @Test
            fun `should return NOT FOUND if no bank with given account number exists`() {
                // given
                val invalidAccountNumber = "does_not_exist"

                // when/then
                mockMvc.delete("$baseUrl/$invalidAccountNumber")
                    .andDo { print() }
                    .andExpect { status { isNotFound() } }
            }
        }
    }
}