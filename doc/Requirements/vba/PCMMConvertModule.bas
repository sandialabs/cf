Attribute VB_Name = "PCMMConvertModule"

'Constants
'PCMM yml attributes
Const PCMMPhasesTag = "Phases"
Const PCMMRolesTag = "Roles"
Const PCMMLevelsTag = "Levels"
Const PCMMElementsTag = "Elements"
Const PCMMPlanningTag = "Planning"
'PCMM Elements Sheets
Const PCMMElementsSheet As String = "PCMM-Elements"
Const PCMMSheetPrefix As String = "PCMM-"
Const PCMMCVERSheet As String = "PCMM-CVER"
Const PCMMPMMFSheet As String = "PCMM-PMMF"
Const PCMMRGFSheet As String = "PCMM-RGF"
Const PCMMSVERSheet As String = "PCMM-SVER"
Const PCMMVALSheet As String = "PCMM-VAL"
Const PCMMUQSheet As String = "PCMM-UQ"
'Global Configuration Sheets
Const PCMMLevelsSheet As String = "PCMM-Levels"
Const PCMMActivitiesSheet As String = "PCMM-Activities"
Const PCMMRolesSheet As String = "PCMM-Roles"
Const PCMMPlanningModelSheet As String = "PCMM-Planning-Data Model"
Const PCMMPlanningQuestionsSheet As String = "PCMM-Planning-Questions"


'Generation Configuration
Const ymlExtension As String = ".yml"
Const GLB_INDENT As String = "  "
Const RootElementName = "PCMM"

'Export PCMM configuration to yml file
Sub ExportPCMMExcelToYaml()

    'Generation Configuration
    Pathname = Application.ActiveWorkbook.Path
    FileName = Left(Application.ActiveWorkbook.Name, (InStrRev(Application.ActiveWorkbook.Name, ".", -1, vbTextCompare) - 1))
    FilePath = Pathname & "\" & FileName & ymlExtension
	Prefix = GLB_INDENT

    'Create Yaml File
    Call CreateYamlFile(FilePath)

    'Export Excel Sheets to Yaml
	Call CreateRootElementToYaml(FilePath, RootElementName)
    Call ExportPhasesToYaml(FilePath, PCMMActivitiesSheet, PCMMPhasesTag, Prefix)
    Call ExportRolesToYaml(FilePath, PCMMRolesSheet, PCMMRolesTag, Prefix)
    Call ExportLevelsToYaml(FilePath, PCMMLevelsSheet, PCMMLevelsTag, Prefix)
    Call ExportElementsToYaml(FilePath, PCMMElementsSheet, PCMMElementsTag, Prefix)
    Call ExportPlannigToYaml(FilePath, PCMMPlanningTag, Prefix)

    'Call ExportContentToYaml
End Sub

'Create the Yaml File to Generate
Private Sub CreateYamlFile(FilePath)

    Filenum = FreeFile
    Open FilePath For Output As #Filenum
    Close #Filenum

End Sub

'Create PCMM root element
Private Sub CreateRootElementToYaml(FilePath, RootElement)

    'Open the file
    Filenum = FreeFile
    Open FilePath For Append As #Filenum

    'Print the Root label
    Print #Filenum, RootElement & ":"

    Close #Filenum

End Sub

'Append the PCMM Phases Sheet content to the generated FilePath
Private Sub ExportPhasesToYaml(FilePath, ByVal PCMMPhasesSheet As String, PCMMPhasesName, Prefix)

	'Check if sheet exists
	ElementSheetExists = SheetExists(PCMMPhasesSheet)
	
	If ElementSheetExists Then
		Dim Sheet As Worksheet
		Set Sheet = Sheets(PCMMPhasesSheet)

		'Open the file
		Filenum = FreeFile
		Open FilePath For Append As #Filenum

		'Print the Levels label
		Print #Filenum, Prefix & PCMMPhasesName & ":"

		Dim rowCount As Long
		rowCount = Sheet.Cells(Sheet.Rows.Count, "B").End(xlUp).Row
		Dim columnCount As Long
		columnCount = Sheet.Cells(1, Sheet.Columns.Count).End(xlToLeft).Column

		'Fields parsing
		For r = 1 To rowCount
			Name = Sheet.Range("B" & r).Value

			'If Name is Empty do not print attribute
			If Name <> "" Then

				'Level name
				Print #Filenum, Prefix & GLB_INDENT & "- """ & Name & """"
			End If
		Next r

		Close #Filenum
	End If
End Sub

'Append the PCMM Roles Sheet content to the generated FilePath
Private Sub ExportRolesToYaml(FilePath, ByVal PCMMRolesSheet As String, PCMMRolesName, Prefix)

	'Check if sheet exists
	ElementSheetExists = SheetExists(PCMMRolesSheet)
	
	If ElementSheetExists Then
		Dim Sheet As Worksheet
		Set Sheet = Sheets(PCMMRolesSheet)

		'Open the file
		Filenum = FreeFile
		Open FilePath For Append As #Filenum

		'Print the Levels label
		Print #Filenum, Prefix & PCMMRolesName & ":"

		Dim rowCount As Long
		rowCount = Sheet.Cells(Sheet.Rows.Count, "B").End(xlUp).Row
		Dim columnCount As Long
		columnCount = Sheet.Cells(1, Sheet.Columns.Count).End(xlToLeft).Column

		'Fields parsing
		Code = 0
		For r = 1 To rowCount
			Name = Sheet.Range("B" & r).Value

			'If Name is Empty do not print attribute
			If Name <> "" Then

				'Level name
				Print #Filenum, Prefix & GLB_INDENT & "- """ & Name & """"
			End If
		Next r

		Close #Filenum
	End If
End Sub

'Append the PCMM Level Sheet content to the generated FilePath
Private Sub ExportLevelsToYaml(FilePath, ByVal PCMMLevelsSheet As String, PCMMLevelsName, Prefix)

	'Check if sheet exists
	ElementSheetExists = SheetExists(PCMMLevelsSheet)
	
	If ElementSheetExists Then
		Dim Sheet As Worksheet
		Set Sheet = Sheets(PCMMLevelsSheet)

		'Levels attributes
		LevelCode = "Code"
		LevelColor = "Color"

		'Open the file
		Filenum = FreeFile
		Open FilePath For Append As #Filenum

		'Print the Levels label
		Print #Filenum, Prefix & PCMMLevelsName & ":"

		Dim rowCount As Long
		rowCount = Sheet.Cells(Sheet.Rows.Count, "B").End(xlUp).Row
		Dim columnCount As Long
		columnCount = Sheet.Cells(1, Sheet.Columns.Count).End(xlToLeft).Column

		'Fields parsing
		Code = 0
		For r = 1 To rowCount
			Name = Sheet.Range("B" & r).Value

			'If Name is Empty do not print attribute
			If Name <> "" Then

				Name = Name & ":"

				'Level name
				Print #Filenum, Prefix & GLB_INDENT & Name

				'Code
				Print #Filenum, Prefix & GLB_INDENT & GLB_INDENT & LevelCode & ": " & Code
				'Level Color
				Print #Filenum, Prefix & GLB_INDENT & GLB_INDENT & LevelColor & ": " & ConvertColorToRgb(Sheet.Range("C" & r).Interior.color)

				'Increment code
				Code = Code + 1
			End If
		Next r

		Close #Filenum
	End If
End Sub

'Append the PCMM Elements Sheet content to the generated FilePath
Private Sub ExportElementsToYaml(FilePath, ByVal PCMMElementsSheet As String, PCMMElementsName, Prefix)

	'Check if sheet exists
	ElementSheetExists = SheetExists(PCMMElementsSheet)
	
	If ElementSheetExists Then
		Dim Sheet As Worksheet
		Set Sheet = Sheets(PCMMElementsSheet)

		'Elements attributes
		ElementNameTag = "Name"
		ElementAbbrevTag = "Abbreviation"
		ElementColorTag = "Color"

		'Subelements attributes
		SubelementsTag = "Subelements"
		SubelementNameTag = "Name"
		SubelementCodeTag = "Code"

		'Levels Attribute
		LevelsTag = "Levels"
		LevelCodeTag = "Code"
		LevelNameTag = "Name"
		LevelDescriptorsTag = "Descriptors"
		LevelDescriptorHeaderRow = 3

		ElementHeaderValue = "Element/Subelement"

		'Open the file
		Filenum = FreeFile
		Open FilePath For Append As #Filenum

		'Print the Elements label
		Print #Filenum, Prefix & PCMMElementsName & ":"

		Dim rowCount As Long
		rowCount = Sheet.Cells(Sheet.Rows.Count, "B").End(xlUp).Row
		Dim columnCount As Long
		columnCount = Sheet.Cells(1, Sheet.Columns.Count).End(xlToLeft).Column
		Dim SubelementSheet As Worksheet
		Dim SubRowCount As Long
		Dim SubColumnCount As Long

		'Fields parsing
		elementAbbrev = ""
		For r = 1 To rowCount

			Abbrev = Sheet.Range("A" & r).Value

			'If Name is Empty or is the header do not print attribute
			If Abbrev <> "" Then

				Name = Sheet.Range("B" & r).Value

				If elementAbbrev = "" Or InStr(Abbrev, elementAbbrev) = 0 Then 'PCMM Elements

					'Element
					Print #Filenum, Prefix & GLB_INDENT & Abbrev & ": "

					'Element Name
					Print #Filenum, Prefix & GLB_INDENT & GLB_INDENT & ElementNameTag & ": " & Name
					'Element Abbreviation
					Print #Filenum, Prefix & GLB_INDENT & GLB_INDENT & ElementAbbrevTag & ": " & Abbrev
					'Element Color
					Print #Filenum, Prefix & GLB_INDENT & GLB_INDENT & ElementColorTag & ": " & ConvertColorToRgb(Sheet.Range("B" & r).Interior.color)
					'Element Level
					Print #Filenum, Prefix & GLB_INDENT & GLB_INDENT & LevelsTag & ": "
					'Level Code Counter
					LevelCode = 0

					Set SubelementSheet = Sheets(PCMMSheetPrefix & Abbrev)

					SubRowCount = SubelementSheet.Cells(SubelementSheet.Rows.Count, "A").End(xlUp).Row
					For rSub = 1 To SubRowCount

						subelementAbbrev = SubelementSheet.Range("A" & rSub).Value
						If subelementAbbrev = Abbrev Then

							LevelName = SubelementSheet.Range("B" & rSub).Value
							If LevelName <> "" Then

								'Level
								Print #Filenum, Prefix & GLB_INDENT & GLB_INDENT & GLB_INDENT & LevelName & ": "

								'Level Code
								Print #Filenum, Prefix & GLB_INDENT & GLB_INDENT & GLB_INDENT & GLB_INDENT & LevelCodeTag & ": " & LevelCode
								'Level Name
								Print #Filenum, Prefix & GLB_INDENT & GLB_INDENT & GLB_INDENT & GLB_INDENT & LevelNameTag & ": " & LevelName


								'PCMM Level Descriptors
								'Descriptors
								Print #Filenum, Prefix & GLB_INDENT & GLB_INDENT & GLB_INDENT & GLB_INDENT & LevelDescriptorsTag & ": "

								SubColumnCount = SubelementSheet.Cells(LevelDescriptorHeaderRow, SubelementSheet.Columns.Count).End(xlToLeft).Column

								For cSub = 3 To SubColumnCount

									DescriptorHeader = SubelementSheet.Cells(LevelDescriptorHeaderRow, cSub).Value
									DescriptorValue = SubelementSheet.Cells(rSub, cSub).Value
									'Replace " char by \" to be valid in Yaml
									DescriptorValue = Replace(DescriptorValue, """", "\""")

									'Descriptor
									Print #Filenum, Prefix & GLB_INDENT & GLB_INDENT & GLB_INDENT & GLB_INDENT & GLB_INDENT & DescriptorHeader & ": """ & DescriptorValue & """"

								Next cSub

								LevelCode = LevelCode + 1

							End If

						End If

					Next rSub
					'Subelement
					Print #Filenum, Prefix & GLB_INDENT & GLB_INDENT & SubelementsTag & ": "

					elementAbbrev = Abbrev

				Else 'PCMM Subelements

					If InStr(Abbrev, elementAbbrev) > 0 Then

						'Subelement
						Print #Filenum, Prefix & GLB_INDENT & GLB_INDENT & GLB_INDENT & Abbrev & ": "

						'Subelement Name
						Print #Filenum, Prefix & GLB_INDENT & GLB_INDENT & GLB_INDENT & GLB_INDENT & SubelementNameTag & ": " & Name
						'Subelement Code
						Print #Filenum, Prefix & GLB_INDENT & GLB_INDENT & GLB_INDENT & GLB_INDENT & SubelementCodeTag & ": " & Abbrev


						'PCMM Levels
						'Level
						Print #Filenum, Prefix & GLB_INDENT & GLB_INDENT & GLB_INDENT & GLB_INDENT & LevelsTag & ": "

						Set SubelementSheet = Sheets(PCMMSheetPrefix & elementAbbrev)

						SubRowCount = SubelementSheet.Cells(SubelementSheet.Rows.Count, "A").End(xlUp).Row

						'Level Code Counter
						LevelCode = 0
						For rSub = 1 To SubRowCount

							subelementAbbrev = SubelementSheet.Range("A" & rSub).Value
							If subelementAbbrev = Abbrev Then

								LevelName = SubelementSheet.Range("B" & rSub).Value
								If LevelName <> "" Then

									'Level
									Print #Filenum, Prefix & GLB_INDENT & GLB_INDENT & GLB_INDENT & GLB_INDENT & GLB_INDENT & LevelName & ": "

									'Level Code
									Print #Filenum, Prefix & GLB_INDENT & GLB_INDENT & GLB_INDENT & GLB_INDENT & GLB_INDENT & GLB_INDENT & LevelCodeTag & ": " & LevelCode
									'Level Name
									Print #Filenum, Prefix & GLB_INDENT & GLB_INDENT & GLB_INDENT & GLB_INDENT & GLB_INDENT & GLB_INDENT & LevelNameTag & ": " & LevelName


									'PCMM Level Descriptors
									'Descriptors
									Print #Filenum, Prefix & GLB_INDENT & GLB_INDENT & GLB_INDENT & GLB_INDENT & GLB_INDENT & GLB_INDENT & LevelDescriptorsTag & ": "

									SubColumnCount = SubelementSheet.Cells(LevelDescriptorHeaderRow, SubelementSheet.Columns.Count).End(xlToLeft).Column

									For cSub = 3 To SubColumnCount

										DescriptorHeader = SubelementSheet.Cells(LevelDescriptorHeaderRow, cSub).Value
										DescriptorValue = SubelementSheet.Cells(rSub, cSub).Value
										'Replace " char by \" to be valid in Yaml
										DescriptorValue = Replace(DescriptorValue, """", "\""")

										'Descriptor
										Print #Filenum, Prefix & GLB_INDENT & GLB_INDENT & GLB_INDENT & GLB_INDENT & GLB_INDENT & GLB_INDENT & GLB_INDENT & DescriptorHeader & ": """ & DescriptorValue & """"

									Next cSub

									LevelCode = LevelCode + 1

								End If

							End If

						Next rSub

					End If

				End If

			End If
		Next r

		Close #Filenum
	End If

End Sub

Private Sub ExportPlannigToYaml(FilePath, PCMMPlannigName, Prefix)
    'Open the file
    Filenum = FreeFile
    Open FilePath For Append As #Filenum

    'Print the Levels label
    Print #Filenum, Prefix & PCMMPlannigName & ":"
    Close #Filenum

    'Generate sub data
    Call ExportPlanningModelToYaml(FilePath, PCMMPlanningModelSheet, Prefix)
    Call ExportPlanningQuestionsToYaml(FilePath, PCMMPlanningQuestionsSheet, "Planning Questions", Prefix)
End Sub

'Export planning fields
Private Sub ExportPlanningModelToYaml(FilePath, ByVal DataModelSheet As String, Prefix)

	'Check if sheet exists
	PlanningSheetExists = SheetExists(DataModelSheet)
	
	If PlanningSheetExists Then
		Dim Sheet As Worksheet
		Set Sheet = Sheets(DataModelSheet)
	
		'Open the file
		Filenum = FreeFile
		Open FilePath For Append As #Filenum

		'Flag is the first row
		Dim isFirst As Boolean
		isFirst = True

		'Get number of row
		Dim rowCount As Long
		rowCount = Sheet.Cells(Sheet.Rows.Count, "B").End(xlUp).Row

		'Get start row
		Dim startRow As Long
		For startRow = Sheet.Cells(1, "A").End(xlDown).Row To rowCount
			'Get first col
			Dim firstCol As Long
			firstCol = Sheet.Cells(1, "A").End(xlToLeft).Column

			'Get number of column
			Dim columnCount As Long
			columnCount = Sheet.Cells(startRow + 1, Sheet.Columns.Count).End(xlToLeft).Column

			'Print into yml
			Title = Sheet.Cells(startRow, firstCol).Value
			If isFirst = True Then
				Print #Filenum, Prefix & GLB_INDENT & Title & ":"
			Else
				Print #Filenum, Prefix & GLB_INDENT & GLB_INDENT & Title & ":"
			End If

			'Fields parsing
			For C = firstCol + 1 To columnCount + columnOffset
				'Row values indexes
				rowName = startRow + 1
				rowRequired = startRow + 2
				rowType = startRow + 3
				rowValues = startRow + 4

				'Row values
				valueName = Sheet.Cells(rowName, C).Value
				valueRequired = Sheet.Cells(rowRequired, C).Value
				valueType = Sheet.Cells(rowType, C).Value

				'Indent
				Dim indent As String
				If isFirst = True Then
					indent = GLB_INDENT & GLB_INDENT
				Else
					indent = GLB_INDENT & GLB_INDENT & GLB_INDENT
				End If

				'Print into yml
				Print #Filenum, Prefix & indent & valueName & ":"
				Print #Filenum, Prefix & indent & GLB_INDENT & "required: " & valueRequired
				Print #Filenum, Prefix & indent & GLB_INDENT & "type: " & valueType

				'Manage field values
				If Cells(rowValues, C).Value <> "" Then

					'Print into yml
					Print #Filenum, Prefix & indent & GLB_INDENT & "values:"

					'While not empty row
					For optRowOffset = 1 To Sheet.Cells(Sheet.Rows.Count, C).End(xlDown).Row

						'Get value
						valueSelect = Sheet.Cells(rowValues + (optRowOffset - 1), C).Value
						If valueSelect <> "" Then
							'Print into yml
							Print #Filenum, Prefix & indent & GLB_INDENT & GLB_INDENT & "- """ & valueSelect & """"
						Else
							Exit For
						End If
					Next optRowOffset
				End If
			Next C

			'Planning Types
			If isFirst = True Then
				Print #Filenum, Prefix & GLB_INDENT & "Planning Types:"
			End If

			'Find next model
			startRow = Sheet.Cells(startRow, "A").End(xlDown).Row - 1
			isFirst = False

		Next startRow

		'Close file
		Close #Filenum
	End If
End Sub

'Export planning questions
Private Sub ExportPlanningQuestionsToYaml(FilePath, ByVal DataQuestionsSheet As String, PCMMPlanningQuestionsName, Prefix)
	
	'Check if sheet exists
	PlanningSheetExists = SheetExists(DataQuestionsSheet)
	
	If PlanningSheetExists Then
		Dim Sheet As Worksheet
		Set Sheet = Sheets(DataQuestionsSheet)

		'Open the file
		Filenum = FreeFile
		Open FilePath For Append As #Filenum

		'Print question key into yml
		Print #Filenum, Prefix & GLB_INDENT & PCMMPlanningQuestionsName & ":"

		'Get number of row
		Dim rowCount As Long
		rowCount = Sheet.Cells(Sheet.Rows.Count, "B").End(xlUp).Row

		'Get first element row
		Dim elementFirstRow As Long
		For elementFirstRow = Sheet.Cells(1, "B").End(xlDown).Row To rowCount
		
            'Last row for the element
            If Sheet.Cells(elementFirstRow + 1, "B").Value = "" Then
                elementLastRow = elementFirstRow
            Else
                elementLastRow = Sheet.Cells(elementFirstRow, "B").End(xlDown).Row
            End If

			'Get first col
			Dim firstCol As Long
			firstCol = Sheet.Cells(elementFirstRow, 1).End(xlToRight).Column

			'Get number of column
			Dim columnCount As Long
			columnCount = Sheet.Cells(elementFirstRow, Sheet.Columns.Count).End(xlToLeft).Column

			'Print the element abbrev
			elementAbbrev = Sheet.Cells(elementFirstRow, firstCol).Value
			If elementAbbrev <> "" Then
				'Print in yml the element abbbreviation
				Print #Filenum, Prefix & GLB_INDENT & GLB_INDENT & elementAbbrev & ":"

				'Has subelement
				If Sheet.Cells(elementFirstRow + 1, firstCol).Value <> "" Then
					For subelementRow = elementFirstRow + 1 To elementLastRow
						subelementAbbrev = Sheet.Cells(subelementRow, firstCol).Value
						Print #Filenum, Prefix & GLB_INDENT & GLB_INDENT & GLB_INDENT & subelementAbbrev & ":"

						'Count number of question
						questionCount = Sheet.Cells(subelementRow, firstCol).End(xlToRight).Column

						'Print each question in Yml
						For questionCol = firstCol + 1 To questionCount
							question = Sheet.Cells(subelementRow, questionCol).Value
							question = Replace(question, """", "\""")
							Print #Filenum, Prefix & GLB_INDENT & GLB_INDENT & GLB_INDENT & GLB_INDENT & "- """ & question & """"
						Next questionCol
					Next subelementRow

				'No subelement
				Else
					'Count number of question
					questionCount = Sheet.Cells(elementFirstRow, firstCol).End(xlToRight).Column

					'Print each question in Yml
					For questionCol = firstCol + 1 To questionCount
						question = Sheet.Cells(elementFirstRow, questionCol).Value
						question = Replace(question, """", "\""")
						Print #Filenum, Prefix & GLB_INDENT & GLB_INDENT & GLB_INDENT & "- """ & question & """"
					Next questionCol
				End If
			End If

			'Find next element
			elementFirstRow = elementLastRow + 1

		Next elementFirstRow

		'Close file
		Close #Filenum
	End If
End Sub

'Convert a color to rgb format
Private Function ConvertColorToRgb(color)
    ConvertColorToRgb = (color Mod 256) & "," & ((color \ 256) Mod 256) & "," & (color \ 65536)
End Function

'Indicate if sheet exists or not
Function SheetExists(sheetToFind As String) As Boolean
    sheetExists = False
    For Each sheet In Worksheets
        If sheetToFind = sheet.name Then
            sheetExists = True
            Exit Function
        End If
    Next sheet
End Function