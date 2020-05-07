import { createSlice } from '@reduxjs/toolkit'
import { renameCategory } from './categories-slice'

const { actions, reducer } = createSlice({
  name: 'command',
  initialState: '',
  reducers: {
    updateCommand(state, action) {
      return action.payload
    }
  },
  extraReducers: builder => {
    builder.addCase(renameCategory, (state, action) => {
      const { name, oldName } = action.payload
      return state.replace(new RegExp('(?<=[*#]?[{+])\\s*'+oldName+'\\s*(?!\\w)', 'gi'), `${name}`)
    })
  }
})

export const { updateCommand } = actions

export default reducer
