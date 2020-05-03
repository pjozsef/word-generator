import {createSlice } from '@reduxjs/toolkit'
import generateWord from '../async/generate-word'

export type RequestStatus = "ON" | "OFF"

type RequestState = {
  generate: RequestStatus
}

const {reducer} = createSlice({
    name: 'requests',
    initialState: {generate: "OFF"} as RequestState,
    reducers: {},
    extraReducers: builder => {
        builder.addCase(generateWord.pending, () => {
          return {
              generate: "ON"
          }
        }).addCase(generateWord.fulfilled, () => {
            return {
                generate: "OFF"
            }
          }).addCase(generateWord.rejected, () => {
            return {
                generate: "OFF"
            }
          })
      }
  })

  export default reducer