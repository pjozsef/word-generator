import {createSlice } from '@reduxjs/toolkit'

const {actions, reducer} = createSlice({
    name: 'command',
    initialState: '',
    reducers: {
      updateCommand(state, action) {
        return action.payload
      }
    }
  })

  export const { updateCommand } = actions

  export default reducer