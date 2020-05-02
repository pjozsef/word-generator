import {createSlice } from '@reduxjs/toolkit'

const {actions, reducer} = createSlice({
    name: 'result',
    initialState: [],
    reducers: {
      newResult(state, action) {
        return action.payload
      }
    }
  })

  export const { newResult } = actions

  export default reducer