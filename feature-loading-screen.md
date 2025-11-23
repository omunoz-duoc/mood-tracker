# Feature: Loading Screen

## Summary
Added a loading screen that displays when the application starts, showing for 3 seconds before navigating to the appropriate screen (Login or Home based on session state).

## Design
- Solid green background (#8BC34A)
- Centered logo placeholder (will be replaced with actual logo)
- App name "Muud" displayed below logo
- Loading spinner at the bottom
- 3-second display duration

## Files Changed

### Created Files
- `app/src/main/java/cl/duoc/dsy1105/moodtracker/ui/screens/LoadingScreen.kt`
  - New composable screen with loading UI
  - LaunchedEffect for 3-second delay
  - Navigation callback to next screen
  - Preview function for design verification

### Modified Files
- `app/src/main/java/cl/duoc/dsy1105/moodtracker/ui/theme/Color.kt`
  - Added `LoadingGreen` color definition (#8BC34A)

- `app/src/main/java/cl/duoc/dsy1105/moodtracker/navigation/Navigation.kt`
  - Added `Loading` route to `Screen` sealed class
  - Imported `LoadingScreen` composable
  - Changed `startDestination` to `Screen.Loading.route`
  - Added navigation logic to route from loading to login/home based on session

## Navigation Flow
1. App starts → Loading Screen (3 seconds)
2. After delay → Navigate to Login (if not logged in) or Home (if logged in)
3. Loading screen is removed from back stack after navigation

## Testing
- Build successful with no errors
- All unit tests passing
- Ready for integration testing on device/emulator

## Next Steps
- Replace emoji placeholder with actual logo image
- Test on physical device/emulator
- Verify smooth transitions to login/home screens
